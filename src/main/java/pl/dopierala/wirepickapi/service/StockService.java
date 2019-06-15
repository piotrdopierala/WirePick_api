package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyReservedException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.ReservationEvent;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.devices.HireRepository;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class StockService {

    private StockRepository stockRepository;
    private HireRepository hireRepository;

    @Autowired
    public StockService(StockRepository stockRepository, HireRepository hireRepository) {

        this.stockRepository = stockRepository;
        this.hireRepository = hireRepository;
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
     * Rents device for given user from supplied date for given Duration
     *
     * @param itemId   device to reserve for user to rent
     * @param start    DateTime start of reservation period
     * @param duration Duration of reservation period
     * @param user     User witch reserves device
     * @return 0 - rent succeeded
     */
    public int reserveItem(Long itemId, LocalDateTime start, Duration duration, User user) throws StockItemIdNotFoundException, DeviceNotAvailableAlreadyReservedException {
        DeviceItem itemFoundById = findStockByItemId(itemId);
        if (isAvailable(itemId, start, duration)) {
            itemFoundById.getHires().add(new ReservationEvent(itemFoundById, start, duration, user));
            stockRepository.save(itemFoundById);
            return 0;
        } else {
            throw new DeviceNotAvailableAlreadyReservedException();
        }
    }

    /**
     * Reserves device for given user between supplied dates
     *
     * @param itemId device to reserve
     * @param start  DateTime start of reservation period
     * @param end    DateTime end of reservation period
     * @param user   User witch reserves device
     * @return 0 - reservation succeeded
     */
    public int reserveItem(Long itemId, LocalDateTime start, LocalDateTime end, User user) throws StockItemIdNotFoundException, UserNotFoundException, DeviceNotAvailableAlreadyReservedException {
        return reserveItem(itemId, start, Duration.between(start, end), user);
    }

    /*
    Reservation independent from actual rent branch
    Idea is that user can reserve some device but can return it earlier. When returning can decide whether to end reservation (shorten it) to day of return or not
    TODO: change ReservationEvent model to persist separate reservation and actual hire period. Maybe a new model ?
    TODO: Function to actual rent in reservation period. For now it should assume device is rents first day of reservation automatically
    TODO: Function to actual return, user should decide weather to shorten  the reservation period.
     */


    /**
     * Checks whether device item is available to reserve given start date and period it of device.
     *
     * @param itemId  Stock item ID that will be checked if available
     * @param when    Start date of reservation period
     * @param howLong Period of reservation
     * @return if deviceDefinition is available or not
     */
    public boolean isAvailable(Long itemId, LocalDateTime when, Duration howLong) {

        if (Objects.isNull(itemId) || Objects.isNull(when) || Objects.isNull(howLong)) {
            return false;
        }
        return isAvailable(itemId, when, when.plus(howLong));
    }

    /**
     * Checks whether device item is available to reserve given start date and period od hire.
     *
     * @param itemId Stock item ID that will be checked if available
     * @param from   Start date of reservation period
     * @param end    End date of reservation period
     * @return if deviceDefinition is available or not
     */
    public boolean isAvailable(Long itemId, LocalDateTime from, LocalDateTime end) {
        if (Objects.isNull(itemId) || Objects.isNull(from) || Objects.isNull(end)) {
            return false;
        }
        return (hireRepository.numberOfOverlappingReservPeriods(itemId, from, end) == 0);
    }

    /**
     * Gets all reservations made by given user
     *
     * @param user User of witch all reservations will be found
     * @return Iterable of ReservationEvent
     */
    public Iterable<ReservationEvent> findAllUserReservations(User user) {
        return hireRepository.findAllByUser(user);
    }


    //TODO write test
    public DeviceItem saveNewItem(DeviceItem newItem) {
        newItem.setId(0);
        return stockRepository.save(newItem);
    }
}
