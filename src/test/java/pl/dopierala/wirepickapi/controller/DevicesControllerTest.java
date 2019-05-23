package pl.dopierala.wirepickapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.configuration.WebMvcConfig;
import pl.dopierala.wirepickapi.service.DeviceService;
import pl.dopierala.wirepickapi.service.StockService;
import pl.dopierala.wirepickapi.service.UserService;

import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DevicesController.class,secure = false)
public class DevicesControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    WebMvcConfig webMvcConfig;

    @MockBean
    DeviceService deviceService;

    @MockBean
    StockService stockService;

    @MockBean
    UserService userService;

    @Before
    public void setup(){
        SampleStock.refreshValues();
    }

    @Test
    public void Should_getAllDevices_return_All_Devices() throws Exception {
        when(deviceService.findAllDevices()).thenReturn(SampleStock.sampleDevices);

        mockMvc.perform(get("/api/device/all"))
                .andDo(print())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$..name").value(hasItem("mock1")))
                .andExpect(jsonPath("$..name").value(hasItem("mock2")))
                .andExpect(jsonPath("$..id").value(hasItem(1)))
                .andExpect(jsonPath("$..id").value(hasItem(2)));
    }

    @Test
    public void Should_getByDeviceId_return_selected_Device() throws Exception {
        final long deviceId = 2L;
        when(deviceService.findDeviceById(deviceId)).thenReturn(Optional.of(SampleStock.getD2()));

        String urlFormat = "/api/device/%s";
        String url = String.format(urlFormat,deviceId);

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(jsonPath("$..name").value(hasItem("mock2")));
    }

}