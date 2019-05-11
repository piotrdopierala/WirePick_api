package pl.dopierala.wirepickapi.service;

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
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyHiredException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;
import pl.dopierala.wirepickapi.model.HireEvent;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static pl.dopierala.wirepickapi.SampleStock.getIterableSize;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepositoryMock;

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
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s2_d1));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s3_d1));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s4_d2));
        assertThat(allItemsStock, Matchers.hasItem(SampleStock.s5_d2));
        assertThat(getIterableSize(allItemsStock), equalTo(5));
    }

    @Test
    public void Should_findStockByItemId_ReturnConcreteDeviceItem() {
        when(stockRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(SampleStock.s2_d1));
        DeviceItem stockItemFoundByItemId = stockService.findStockByItemId(2L);
        assertThat(stockItemFoundByItemId, is(CoreMatchers.equalTo(SampleStock.s2_d1)));
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

        stockService.rentItem(testDeviceId, LocalDateTime.now(), LocalDateTime.now(), SampleUsers.u1);
    }

    @Test(expected = DeviceNotAvailableAlreadyHiredException.class)
    public void Should_rentItem_ThrowException_IfDeviceAlreadyHired() {
        final long testDeviceId = 1L;
        final long testUserId = 1L;

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<HireEvent> hires = s1_d1_clone.getHires();
        hires.add(new HireEvent(LocalDateTime.of(2017, 05, 20, 0, 0),
                LocalDateTime.of(2017, 05, 22, 0, 0),
                SampleUsers.u1));

        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.of(s1_d1_clone));

        stockService.rentItem(testDeviceId,
                LocalDateTime.of(2017, 05, 20, 0, 0),
                LocalDateTime.of(2017, 05, 21, 0, 0),
                SampleUsers.u1);
    }

    @Test
    public void Should_rentItem_hireSuccessfully() {
        final long testDeviceId = 1L;
        final long testUserId = 1L;
        final LocalDateTime hireStartDate = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime hireEndDate = LocalDateTime.of(2017, 05, 21, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        int beginHiresSize = s1_d1_clone.getHires().size();
        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.of(s1_d1_clone));
        when(stockRepositoryMock.numberOfOverlappingHirePeriods(testDeviceId,hireStartDate,hireEndDate)).thenReturn(1);

        stockService.rentItem(testDeviceId,
                hireStartDate,
                hireEndDate,
                SampleUsers.u1);

        int endHiresSize = s1_d1_clone.getHires().size();

        Assert.assertEquals(beginHiresSize + 1, endHiresSize);
    }

    @Test
    public void Should_isAvailable_ReturnTrue_When_DeviceFree() {
        final long testDeviceId = 1L;
        final LocalDateTime hireStart = LocalDateTime.of(2017, 05, 20, 0, 0);
        final LocalDateTime hireEnd = LocalDateTime.of(2017, 05, 22, 0, 0);
        final LocalDateTime freeStart = LocalDateTime.of(2017, 06, 21, 0, 0);
        final LocalDateTime freeEnd = LocalDateTime.of(2017, 06, 22, 0, 0);

        DeviceItem s1_d1_clone = SampleStock.s1_d1.clone();
        List<HireEvent> hires = s1_d1_clone.getHires();
        hires.add(new HireEvent(hireStart,
                hireEnd,
                SampleUsers.u1));

        when(stockRepositoryMock.findById(testDeviceId)).thenReturn(Optional.of(s1_d1_clone));
        when(stockRepositoryMock.numberOfOverlappingHirePeriods(testDeviceId,freeStart,freeEnd)).thenReturn(1);

        Assert.assertTrue(stockService.isAvailable(testDeviceId, freeStart, freeEnd));
    }

    //thould be tested with H2 test DB. Not on real data.
    //because numberOfOverlappingHirePeriods works on DB
    @Test
    public void Should_isAvailable_ReturnFalse_When_DeviceAlreadyHired() {

    }


}