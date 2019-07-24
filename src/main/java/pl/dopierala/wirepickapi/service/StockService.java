package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyBookedException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.*;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.BookEvent;
import pl.dopierala.wirepickapi.model.BorrowEvent;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.devices.BookingsRepository;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StockService {

    private StockRepository stockRepository;
    private BookingsRepository bookingsRepository;

    @Autowired
    public StockService(StockRepository stockRepository, BookingsRepository bookingsRepository) {

        this.stockRepository = stockRepository;
        this.bookingsRepository = bookingsRepository;
    }

    public Iterable<DeviceItem> findAllStock() {
        return stockRepository.findAll();
    }

    public DeviceItem findStockByItemId(Long id) throws StockItemIdNotFoundException {
        Optional<DeviceItem> itemFoundById = stockRepository.findById(id);
        if (itemFoundById.isPresent()) {
            return itemFoundById.get();
        } else {
            throw new StockItemIdNotFoundException("Stock item id '" + id + "' not found.");
        }
    }

    public BookEvent findBookingById(Long id) {
        Optional<BookEvent> bookingFoundById = bookingsRepository.findById(id);
        if (bookingFoundById.isPresent()) {
            return bookingFoundById.get();
        } else {
            throw new BookingNotFoundException("Booking event id '" + id + "' not found.");
        }
    }

    public Iterable<DeviceItem> findStockByDeviceDefinition(Long deviceDefinitionId) throws StockItemByDeviceIdNotFoundException {
        Iterable<DeviceItem> itemsFoundByDeviceDefinitionId = stockRepository.findByDeviceDefinition_Id(deviceDefinitionId);
        if (itemsFoundByDeviceDefinitionId.iterator().hasNext()) {
            return itemsFoundByDeviceDefinitionId;
        } else {
            throw new StockItemByDeviceIdNotFoundException("Stock item by device definition id '" + deviceDefinitionId + "' not found even single one.");
        }
    }

    public Iterable<DeviceItem> findFreeStockByDeviceDefinition(Long deviceDefinitionId, LocalDateTime from, LocalDateTime to) {
        return stockRepository.findFreeItemsByDeviceIdAndHirePeriod(deviceDefinitionId, from, to);
    }


    /**
     * Books device for given user from supplied date for given Duration
     *
     * @param itemId   device to reserve for user to rent
     * @param start    DateTime start of reservation period
     * @param duration Duration of reservation period
     * @param user     User witch reserves device
     * @return 0 - rent succeeded
     */
    public int bookItem(Long itemId, LocalDateTime start, Duration duration, User user) throws StockItemIdNotFoundException, DeviceNotAvailableAlreadyBookedException {
        DeviceItem itemFoundById = findStockByItemId(itemId);
        if (isBookAvailable(itemId, start, duration)) {
            itemFoundById.getBookings().add(new BookEvent(itemFoundById, start, duration, user));
            stockRepository.save(itemFoundById);
            return 0;
        } else {
            throw new DeviceNotAvailableAlreadyBookedException();
        }
    }

    /**
     * Books device for given user between supplied dates
     *
     * @param itemId device to book
     * @param start  DateTime start of book period
     * @param end    DateTime end of book period
     * @param user   User witch books device
     * @return 0 - book succeeded, device booked
     */
    public int bookItem(Long itemId, LocalDateTime start, LocalDateTime end, User user) throws StockItemIdNotFoundException, UserNotFoundException, DeviceNotAvailableAlreadyBookedException {
        return bookItem(itemId, start, Duration.between(start, end), user);
    }

    /**
     * Marks item as borrowed in given period
     *
     * @param user user that borrows given item
     * @param itemId item id that is being borrowed
     * @param start start of borrow period
     * @param end end of borrow period
     * @return 0 - borrow succeeded
     */
    public int borrowItem(User user, Long itemId, LocalDateTime start, LocalDateTime end) throws BookingNotFoundException {

        Optional<BookEvent> foundUserItemBookingInPeriod = findUserItemBookingInPeriod(user, itemId, start, end);

        if (!foundUserItemBookingInPeriod.isPresent()) {
            throw new BookingNotFoundException("Reservation for user '" + user.getLogin() + "' of item id'" + itemId + "' in period from " + start + " to " + end + " not found.");
        }
        BookEvent foundBooking = foundUserItemBookingInPeriod.get();
        return borrow(foundBooking, start, end);
    }

    /**
     * Marks given item as borrowed from start DateTime to end of booking period
     *
     * @param user user that borrows item
     * @param itemId item id that is being borrowed
     * @param start start of borrow period
     * @return 0 - borrow succeeded
     */
    public int borrowItemToEndOfBookPeriod(User user, Long itemId, LocalDateTime start) throws BookingNotFoundException, DeviceNotAvailableAlreadyRentException {
        LocalDateTime end = start;
        Optional<BookEvent> foundUserItemBookingInPeriod = findUserItemBookingInPeriod(user, itemId, start, end);
        if (!foundUserItemBookingInPeriod.isPresent()) {
            throw new BookingNotFoundException("Reservation for user '" + user.getLogin() + "' of item id'" + itemId + "' in date " + start + " not found.");
        }
        BookEvent foundBooking = foundUserItemBookingInPeriod.get();
        end = foundBooking.getBookEnd();
        return borrow(foundBooking, start, end);
    }

    private int borrow(BookEvent foundBooking, LocalDateTime start, LocalDateTime end) throws DeviceNotAvailableAlreadyRentException {
        if (!isRentAvailable(foundBooking.getId(), start, end)) {
            throw new DeviceNotAvailableAlreadyRentException();
        }

        foundBooking.getBorrows().add(new BorrowEvent(foundBooking, start, end));
        bookingsRepository.save(foundBooking);
        return 0;
    }

    /**
     * Returns device, closes reservation with supplied date and time
     *
     * @param user user witch returns item
     * @param itemId id of item to return
     * @param returnDateTime timestamp of return (must be within book period)
     * @return 0 - return succeeded
     */
    public int returnItem(User user, Long itemId, LocalDateTime returnDateTime){
        BookEvent foundBookEvent = bookingsRepository.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(user, itemId, returnDateTime, returnDateTime);
        if(Objects.isNull(foundBookEvent)){
            throw new BookingNotFoundException("Item of id '"+itemId+"' is not currently booked by user '"+user.getLogin()+"'.");
        }
        foundBookEvent.setBookEnd(returnDateTime);
        List<BorrowEvent> borrows = foundBookEvent.getBorrows();
        if(borrows.size()>0){
            BorrowEvent lastBorrowEvent = borrows.get(borrows.size() - 1);
            lastBorrowEvent.setBorrowEnd(returnDateTime);
        }
        bookingsRepository.save(foundBookEvent);
        return 0;
    }

    /**
     * Checks whether device item is available to book given start date and period it of device.
     *
     * @param itemId  Stock item ID that will be checked if available
     * @param when    Start date of reservation period
     * @param howLong Period of reservation
     * @return if deviceDefinition is available or not
     */
    public boolean isBookAvailable(Long itemId, LocalDateTime when, Duration howLong) {

        if (Objects.isNull(itemId) || Objects.isNull(when) || Objects.isNull(howLong)) {
            return false;
        }
        return isBookAvailable(itemId, when, when.plus(howLong));
    }

    /**
     * Checks whether device item is available to book given start date and period od hire.
     *
     * @param itemId Stock item ID that will be checked if available
     * @param from   Start date of reservation period
     * @param end    End date of reservation period
     * @return if deviceDefinition is available or not
     */
    public boolean isBookAvailable(Long itemId, LocalDateTime from, LocalDateTime end) {
        if (Objects.isNull(itemId) || Objects.isNull(from) || Objects.isNull(end)) {
            return false;
        }
        return (bookingsRepository.numberOfOverlappingBookPeriods(itemId, from, end) == 0);
    }

    public boolean isRentAvailable(Long bookId, LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(bookId) || Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }
        return (bookingsRepository.numberOfOverlappingRentPeriods(bookId, start, end) == 0);
    }

    /**
     * Gets all books (reservations) made by given user
     *
     * @param user User of witch all books will be found
     * @return Iterable of BookEvent
     */
    public Iterable<BookEvent> findAllUserBookings(User user) {
        return bookingsRepository.findAllByUser(user);
    }


    public DeviceItem saveNewItem(DeviceItem newItem) {
        newItem.setId(0);
        return stockRepository.save(newItem);
    }


    /**
     * Gets all bookings of given item by given User
     *
     * @param user   user who's bookings to find
     * @param itemId item witch bookings to find
     * @return iterable of BookEvent (all bookings)
     */
    public Iterable<BookEvent> findAllUserItemBookings(User user, Long itemId) {
        return bookingsRepository.findAllByUserAndItemBooked_Id(user, itemId);
    }

    /**
     * Gets book event of given item by given User that covers given period
     *
     * @param user   iser who's booking to find
     * @param itemId item witch booking to find
     * @param start  start date of period that book event should include
     * @param end    end date of period that book event should include
     * @return
     */
    public Optional<BookEvent> findUserItemBookingInPeriod(User user, Long itemId, LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(bookingsRepository.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(user, itemId, start, end));
    }

    /**
     * Gets all bookings of particular device item
     *
     * @param itemId id of item witch bookings to find
     * @return Collection of bookings
     */
    public Iterable<BookEvent> findAllBookingsOfDeviceItemId(Long itemId){
        return bookingsRepository.findAllByItemBooked_Id(itemId);
    }
}
