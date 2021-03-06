package pl.dopierala.wirepickapi.service;

import org.assertj.core.util.Lists;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.SampleUsers;
import pl.dopierala.wirepickapi.Utils;
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyBookedException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.BookingNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.DeviceNotAvailableAlreadyRentException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;
import pl.dopierala.wirepickapi.model.BookEvent;
import pl.dopierala.wirepickapi.model.BorrowEvent;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.devices.BookingsRepository;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.dopierala.wirepickapi.SampleStock.*;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepositoryMock;
    @Mock
    private BookingsRepository bookingsRepositoryMock;

    @InjectMocks
    private StockService stockService;

    @BeforeClass
    public static void prepareSampleData() {
        SampleStock.refreshValues();
    }

    @Test
    public void Should_findAllStock_ReturnAllMockDevices() {
        when(stockRepositoryMock.findAll()).thenReturn(SampleStock.sampleStock);

        Iterable<DeviceItem> allItemsStock = stockService.findAllStock();

        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s1_d1));
        assertThat(allItemsStock, Matchers.hasItem(s2_d1));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s3_d1));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s4_d2));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s5_d2));
        assertThat(getIterableSize(allItemsStock), equalTo(5));
    }

    @Test
    public void Should_findStockByItemId_ReturnConcreteDeviceItem() {
        when(stockRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(s2_d1));
        DeviceItem stockItemFoundByItemId = stockService.findStockByItemId(2L);
        assertThat(stockItemFoundByItemId, is(CoreMatchers.equalTo(s2_d1)));
    }

    @Test(expected = StockItemIdNotFoundException.class)
    public void Should_findStockByItemId_ThrowException_IfNotFound() {
        when(stockRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        stockService.findStockByItemId(1L);
    }

    @Test
    public void Should_findStockByDeviceDefinition_ReturnConcreteDevices_ByDefinitionId() {
        when(stockRepositoryMock.findByDeviceDefinition_Id(2L)).thenReturn(SampleStock.sampleStockOfDeviceDef2);

        Iterable<DeviceItem> stockFoundByDeviceDefinition = stockService.findStockByDeviceDefinition(2L);

        assertThat(SampleStock.sampleStockOfDeviceDef2, hasItem(SampleStock.s4_d2));
        assertThat(SampleStock.sampleStockOfDeviceDef2, hasItem(SampleStock.s5_d2));
        assertEquals(getIterableSize(stockFoundByDeviceDefinition), 2);
    }

    @Test(expected = StockItemByDeviceIdNotFoundException.class)
    public void Should_findStockByDeviceDefinition_ThrowException_IfNoDevicesFound() {
        when(stockRepositoryMock.findByDeviceDefinition_Id(1L)).thenReturn(new ArrayList<>());

        stockService.findStockByDeviceDefinition(1L);
    }

    @Test(expected = StockItemIdNotFoundException.class)
    public void Should_rentItem_ThrowException_IfNoDeviceFound() {
        final long testDeviceId = 1L;
        final long testUserId = 1L;
        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.empty());

        stockService.bookItem(testDeviceId, LocalDateTime.now(), LocalDateTime.now(), SampleUsers.u1);
    }

    @Test(expected = DeviceNotAvailableAlreadyBookedException.class)
    public void Should_rentItem_ThrowException_IfDeviceAlreadyBooked() {
        final long testDeviceId = 1L;
        final LocalDateTime bookStartDate = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEndDate = LocalDateTime.of(2017, 05, 21, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> bookings = s1_d1_clone.getBookings();
        bookings.add(new BookEvent(s1_d1_clone, LocalDateTime.of(2017, 05, 20, 0, 0),
                LocalDateTime.of(2017, 05, 22, 0, 0),
                SampleUsers.u1));

        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.of(s1_d1_clone));
        when(bookingsRepositoryMock.numberOfOverlappingBookPeriods(testDeviceId, bookStartDate, bookEndDate)).thenReturn(1);

        stockService.bookItem(testDeviceId,
                bookStartDate,
                bookEndDate,
                SampleUsers.u1);
    }

    @Test
    public void Should_bookItem_hireSuccessfully() {
        final long testDeviceId = 1L;
        final long testUserId = 1L;
        final LocalDateTime bookStartDate = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEndDate = LocalDateTime.of(2017, 05, 21, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        int beginBookingsSize = s1_d1_clone.getBookings().size();
        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.of(s1_d1_clone));
        when(bookingsRepositoryMock.numberOfOverlappingBookPeriods(testDeviceId, bookStartDate, bookEndDate)).thenReturn(0);

        stockService.bookItem(testDeviceId,
                bookStartDate,
                bookEndDate,
                SampleUsers.u1);

        int endHiresSize = s1_d1_clone.getBookings().size();

        Assert.assertEquals(beginBookingsSize + 1, endHiresSize);
    }

    @Test
    public void Should_isAvailable_ReturnTrue_When_DeviceFree() {
        final long testDeviceId = 1L;
        final LocalDateTime hireStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime hireEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final LocalDateTime freeStart = LocalDateTime.of(2017, 06, 21, 0, 0);
        final LocalDateTime freeEnd = LocalDateTime.of(2017, 06, 22, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> hires = s1_d1_clone.getBookings();
        hires.add(new BookEvent(s1_d1_clone, hireStart,
                hireEnd,
                SampleUsers.u1));

        when(bookingsRepositoryMock.numberOfOverlappingBookPeriods(testDeviceId, freeStart, freeEnd)).thenReturn(0);

        Assert.assertTrue(stockService.isBookAvailable(testDeviceId, freeStart, freeEnd));
    }

    @Test
    public void Should_isAvailable_ReturnFalse_When_DeviceAlreadyHired() {
        final long testDeviceId = 1L;
        final LocalDateTime hireStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime hireEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final LocalDateTime freeStart = LocalDateTime.of(2017, 05, 21, 0, 0);
        final LocalDateTime freeEnd = LocalDateTime.of(2017, 05, 24, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> hires = s1_d1_clone.getBookings();
        hires.add(new BookEvent(
                s1_d1_clone, hireStart,
                hireEnd,
                SampleUsers.u1));

        when(bookingsRepositoryMock.numberOfOverlappingBookPeriods(testDeviceId, freeStart, freeEnd)).thenReturn(1);

        Assert.assertFalse(stockService.isBookAvailable(testDeviceId, freeStart, freeEnd));
    }


    @Test
    public void Should_findAllUserBookings_return_User_BookEvents() {
        final LocalDateTime bookStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEnd = LocalDateTime.of(2017, 05, 22, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> bookings = s1_d1_clone.getBookings();
        bookings.add(new BookEvent(s1_d1_clone, bookStart,
                bookEnd,
                SampleUsers.u2));

        when(bookingsRepositoryMock.findAllByUser(SampleUsers.u1)).thenReturn(Lists.emptyList());
        when(bookingsRepositoryMock.findAllByUser(SampleUsers.u2)).thenReturn(bookings);

        Assert.assertEquals(Lists.emptyList(), stockService.findAllUserBookings(SampleUsers.u1));
        Assert.assertEquals(bookings, stockService.findAllUserBookings(SampleUsers.u2));
    }

    @Test
    public void Should_findFreeStockByDeviceDefinition_ReturnAvailableItems() {
        final long testDeviceId = 1L;
        final LocalDateTime bookStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEnd = LocalDateTime.of(2017, 05, 22, 0, 0);

        when(stockRepositoryMock.findFreeItemsByDeviceIdAndHirePeriod(testDeviceId, bookStart, bookEnd)).thenReturn(Arrays.asList(s1_d1, s2_d1));

        Assert.assertEquals(Utils.getIterableSize(stockService.findFreeStockByDeviceDefinition(testDeviceId, bookStart, bookEnd)), 2);
    }

    @Test
    public void Should_findAllUserItemBookings_returnBookings() {
        final long testDeviceId = 1L;
        final LocalDateTime bookStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEnd = LocalDateTime.of(2017, 05, 22, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> bookings = s1_d1_clone.getBookings();
        bookings.add(new BookEvent(s1_d1_clone, bookStart,
                bookEnd,
                SampleUsers.u2));

        when(bookingsRepositoryMock.findAllByUserAndItemBooked_Id(SampleUsers.u1, testDeviceId)).thenReturn(Lists.emptyList());
        when(bookingsRepositoryMock.findAllByUserAndItemBooked_Id(SampleUsers.u2, testDeviceId)).thenReturn(bookings);

        Assert.assertEquals(Lists.emptyList(), stockService.findAllUserItemBookings(SampleUsers.u1, testDeviceId));
        Assert.assertEquals(bookings, stockService.findAllUserItemBookings(SampleUsers.u2, testDeviceId));
    }

    @Test
    public void Should_findUserItemBookingInPeriod_returnBooking() {
        final long testDeviceId = 1L;
        final LocalDateTime bookStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime bookEnd = LocalDateTime.of(2017, 05, 22, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<BookEvent> bookings = s1_d1_clone.getBookings();
        bookings.add(new BookEvent(s1_d1_clone, bookStart,
                bookEnd,
                SampleUsers.u2));

        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(SampleUsers.u2, testDeviceId, bookStart, bookEnd)).thenReturn(bookings.get(0));

        Assert.assertEquals(Optional.of(bookings.get(0)), stockService.findUserItemBookingInPeriod(SampleUsers.u2, testDeviceId, bookStart, bookEnd));
    }

    @Test
    public void Should_borrow_item_borrow() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final LocalDateTime borrowStart = LocalDateTime.of(2017, 05, 2, 0, 0);
        final LocalDateTime borrowEnd = LocalDateTime.of(2017, 05, 5, 0, 0);
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();
        final int sampleBookingBorrowsStartCount = sampleBooking.getBorrows().size();

        //when
        sampleBooking.setId(0);
        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, borrowStart, borrowEnd)).thenReturn(sampleBooking);
        when(bookingsRepositoryMock.numberOfOverlappingRentPeriods(sampleBooking.getId(), borrowStart, borrowEnd)).thenReturn(0);

        stockService.borrowItem(sampleUser, testItemId, borrowStart, borrowEnd);

        //then
        final List<BorrowEvent> borrowsAfterAdd = sampleBooking.getBorrows();
        final BorrowEvent borrowEvent = borrowsAfterAdd.get(borrowsAfterAdd.size() - 1);

        Assert.assertEquals(sampleBookingBorrowsStartCount + 1, borrowsAfterAdd.size());
        Assert.assertEquals(borrowStart, borrowEvent.getBorrowStart());
        Assert.assertEquals(borrowEnd, borrowEvent.getBorrowEnd());
        verify(bookingsRepositoryMock).save(sampleBooking);
    }

    @Test(expected = BookingNotFoundException.class)
    public void Should_borrow_not_find_booking() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final LocalDateTime borrowStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime borrowEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();

        //when
        sampleBooking.setId(0);
        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, borrowStart, borrowEnd)).thenReturn(null);


        //then (expected exception)
        stockService.borrowItem(sampleUser, testItemId, borrowStart, borrowEnd);
    }

    @Test(expected = DeviceNotAvailableAlreadyRentException.class)
    public void Should_borrow_throw_excp_already_rent() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final LocalDateTime borrowStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime borrowEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();

        //when
        sampleBooking.setId(0);
        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, borrowStart, borrowEnd)).thenReturn(sampleBooking);
        when(bookingsRepositoryMock.numberOfOverlappingRentPeriods(sampleBooking.getId(), borrowStart, borrowEnd)).thenReturn(1);

        //then (expected exception)
        stockService.borrowItem(sampleUser, testItemId, borrowStart, borrowEnd);
    }

    @Test
    public void Should_borrowItemToEndOfBookPeriod_borrow_to_end_book() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final LocalDateTime borrowStart = LocalDateTime.of(2017, 05, 2, 0, 0);
        final LocalDateTime borrowEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();
        final LocalDateTime bookEnd = sampleBooking.getBookEnd();
        final int sampleBookingBorrowsStartCount = sampleBooking.getBorrows().size();

        //when
        sampleBooking.setId(0);
        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, borrowStart, borrowStart)).thenReturn(sampleBooking);
        when(bookingsRepositoryMock.numberOfOverlappingRentPeriods(sampleBooking.getId(), borrowStart, borrowEnd)).thenReturn(0);

        stockService.borrowItemToEndOfBookPeriod(sampleUser, testItemId, borrowStart);

        //then
        final List<BorrowEvent> borrowsAfterAdd = sampleBooking.getBorrows();
        final BorrowEvent borrowEvent = borrowsAfterAdd.get(borrowsAfterAdd.size() - 1);

        Assert.assertEquals(sampleBookingBorrowsStartCount + 1, borrowsAfterAdd.size());
        Assert.assertEquals(borrowStart, borrowEvent.getBorrowStart());
        Assert.assertEquals(bookEnd, borrowEvent.getBorrowEnd());
        verify(bookingsRepositoryMock).save(sampleBooking);
    }

    @Test
    public void Should_return_successfully_return() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();
        final LocalDateTime returnDateTime = LocalDateTime.of(2017, 05, 5, 12, 0);

        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, returnDateTime, returnDateTime)).thenReturn(sampleBooking);

        Assert.assertTrue(returnDateTime.isBefore(sampleBooking.getBookEnd()));
        Assert.assertTrue(returnDateTime.isBefore(sampleBooking.getBorrows().get(sampleBooking.getBorrows().size()-1).getBorrowEnd()));

        stockService.returnItem(sampleUser,testItemId, returnDateTime);

        Assert.assertEquals(returnDateTime,sampleBooking.getBookEnd());
        Assert.assertEquals(returnDateTime,sampleBooking.getBorrows().get(sampleBooking.getBorrows().size()-1).getBorrowEnd());
        verify(bookingsRepositoryMock).save(sampleBooking);
    }

    @Test(expected = BookingNotFoundException.class)
    public void Should_return_throw_bookingNotFound_exception() throws CloneNotSupportedException {
        //given
        final long testItemId = 1L;
        final User sampleUser = SampleStock.u1;
        final BookEvent sampleBooking = s1_u1_book1.clone();
        final LocalDateTime returnDateTime = LocalDateTime.of(2017, 05, 5, 12, 0);

        when(bookingsRepositoryMock.findBookEventByUserAndItemBooked_IdAndBookStartLessThanEqualAndBookEndGreaterThanEqual(sampleUser, testItemId, returnDateTime, returnDateTime)).thenReturn(null);

        stockService.returnItem(sampleUser,testItemId, returnDateTime);
    }

    @Test
    public void Should_saveNewItem_save_new_device(){
        final DeviceItem newItem = s1_d1.clone();

        stockService.saveNewItem(newItem);

        verify(stockRepositoryMock).save(newItem);
    }
}