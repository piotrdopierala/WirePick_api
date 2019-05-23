package pl.dopierala.wirepickapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.SampleUsers;
import pl.dopierala.wirepickapi.configuration.WebMvcConfig;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.service.StockService;
import pl.dopierala.wirepickapi.service.UserService;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = StockController.class,secure = false)
public class StockControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WebMvcConfig webMvcConfig;

    @MockBean
    StockService stockService;

    @MockBean
    UserService userService;

    @Before
    public void setup(){
        SampleStock.refreshValues();
    }

    @Test
    public void Should_getAllDevices_return_All_DevicesOnStock() throws Exception {
        when(stockService.findAllStock()).thenReturn(SampleStock.sampleStock);

        mockMvc.perform(get("/api/stock/all"))
                .andDo(print())
                .andExpect(jsonPath("$").value(hasSize(5)))
                .andExpect(jsonPath("$..name").value(hasItem("mock1")))
                .andExpect(jsonPath("$..deviceDefinition.id").value(hasItem(1)));
    }


    @Test
    public void Should_putHireDevice_ReturnBadRequestCode400_when_DateParseError() throws Exception {
        final long stockItemId = 1L;
        final String hireStartDate = "2017-05-19";
        final String hireEndDate = "2017a-05-21";
        final long userId = 1L;

        String urlFormat = "/api/stock/hire/%s/user/%s/from/%s/to/%s";
        String url = String.format(urlFormat,stockItemId,userId,hireStartDate,hireEndDate);

        mockMvc.perform(put(url))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Should_putHireDevice_Hire_Successfully() throws Exception {
        final long stockItemId = 1L;
        final String hireStartDate = "2017-05-19";
        final String hireEndDate = "2017-05-21";
        final long userId = 1L;

        String urlFormat = "/api/stock/hire/%s/user/%s/from/%s/to/%s";
        String url = String.format(urlFormat,stockItemId,userId,hireStartDate,hireEndDate);

        when(userService.findUserById(userId)).thenReturn(SampleUsers.u1);

        mockMvc.perform(put(url))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

}