package pl.dopierala.wirepickapi.repositories.devices;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest()
//@DataJpaTest
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DevicesDefinitionRepository devicesDefinitionRepository;

    @Test
    public void Should_persist_and_find_deviceItem(){
        DeviceItem deviceItem = SampleStock.s1_d1;

        devicesDefinitionRepository.save(deviceItem.getDeviceDefinition());
        stockRepository.save(deviceItem);
        Optional<DeviceItem> foundById = stockRepository.findById(SampleStock.s1_d1.getId());

        Assert.assertTrue(foundById.isPresent());
    }
}