package pl.dopierala.wirepickapi.service;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.repositories.devices.DevicesDefinitionRepository;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceTest {

    @Mock
    DevicesDefinitionRepository devicesDefinitionRepositoryMock;

    @InjectMocks
    DeviceService deviceService;

    private static DeviceDefinition d1;
    private static DeviceDefinition d2;
    private static Iterable<DeviceDefinition> sampleDevices;

    public DeviceServiceTest() {
    }

    @BeforeClass
    public static void prepareSampleData(){
        d1 = new DeviceDefinition();
        d1.setId(1);
        d1.setName("mock1");
        d2 = new DeviceDefinition();
        d2.setId(2);
        d2.setName("mock2");
        sampleDevices = new ArrayList<>(Arrays.asList(d1,d2));
    }

    @Test
    public void Should_findAllDevices_ReturnAllMockDevices() {
        when(devicesDefinitionRepositoryMock.findAll()).thenReturn(sampleDevices);

        Iterable<DeviceDefinition> allDevicesResult = deviceService.findAllDevices();

        int allDevicesSize = 0;
        if(allDevicesResult instanceof List) {
            allDevicesSize=((List)allDevicesResult).size();
        }else {
            for (DeviceDefinition deviceDefinition : allDevicesResult) {
                allDevicesSize++;
            }
        }

        assertThat(allDevicesResult, Matchers.hasItem(d1));
        assertThat(allDevicesResult, Matchers.hasItem(d2));
        assertThat(allDevicesSize,equalTo(2));
    }

    @Test
    public void Should_findDeviceById_ReturnConcreteDeviceDefinition() {
        when(devicesDefinitionRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(d2));

        Optional<DeviceDefinition> deviceFoundById = deviceService.findDeviceById(2L);

        assertTrue(deviceFoundById.isPresent());
        assertThat(deviceFoundById.get(),is(equalTo(d2)));
    }
}