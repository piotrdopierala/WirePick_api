package pl.dopierala.wirepickapi.repositories.devices;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.repositories.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DevicesDefinitionRepository devicesDefinitionRepository;
    @Autowired
    private UserRepository userRepository;


    @Before
    public void initialize_data() {
        //prepare users
        userRepository.save(SampleStock.u1);
        userRepository.save(SampleStock.u2);

        //prepare device definintions
        devicesDefinitionRepository.save(SampleStock.d1);
        devicesDefinitionRepository.save(SampleStock.d2);

        //prepare stock items
        stockRepository.save(SampleStock.s1_d1);
        stockRepository.save(SampleStock.s2_d1);
        stockRepository.save(SampleStock.s3_d1);
        stockRepository.save(SampleStock.s4_d2);
        stockRepository.save(SampleStock.s5_d2);
    }

    @Test
    public void Should_find_deviceItem() {
        Optional<DeviceItem> foundById = stockRepository.findById(SampleStock.s1_d1.getId());

        if (!foundById.isPresent()) {
            Assert.fail("findById didn't found anything");
        }
        Assert.assertEquals(foundById.get(), SampleStock.s1_d1);
    }

    @Test
    public void Should_findFreeItems_find_three_devices(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2019, 07, 10, 0, 0),
                LocalDateTime.of(2019, 07, 11, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),3);
    }

    @Test
    public void Should_findFreeItems_find_two_devices(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2010, 07, 10, 0, 0),
                LocalDateTime.of(2019, 07, 11, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),2);
    }

    @Test
    public void Should_findFreeItems_find_two_devices_third_beginning_overlap(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 04, 20, 0, 0),
                LocalDateTime.of(2017, 05, 2, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),2);
    }

    @Test
    public void Should_findFreeItems_find_two_devices_third_ending_overlap(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 05, 9, 0, 0),
                LocalDateTime.of(2017, 05, 11, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),2);
    }

    @Test
    public void Should_findFreeItems_find_two_devices_third_whole_overlap(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 04, 25, 0, 0),
                LocalDateTime.of(2017, 06, 01, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),2);
    }

    @Test
    public void Should_findFreeItems_find_three_devices_period_in_window_between(){
        Iterable<DeviceItem> freeItemsFound = stockRepository.findFreeItemsByDeviceIdAndHirePeriod(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 05, 11, 0, 0),
                LocalDateTime.of(2017, 05, 20, 0, 0)
        );
        Assert.assertEquals(SampleStock.getIterableSize(freeItemsFound),3);
    }

}