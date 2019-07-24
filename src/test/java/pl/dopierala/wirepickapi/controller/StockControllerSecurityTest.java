package pl.dopierala.wirepickapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.service.StockService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockControllerSecurityTest {

    private MockMvc mockMvc;

    @MockBean
    StockService stockServiceMock;

    @Autowired
    WebApplicationContext webAppContext;

    @Before
    public void setup(){
        this.mockMvc =  MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private void loginAsUser(User user){
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    public void Should_putHireDevice_with_correct_user_access_OK_code_200() throws Exception {
        final long loggedUserId = 1L;
        final long stockItemId = 1L;
        final String hireStartDate = "2017-05-19";
        final String hireEndDate = "2017-05-21";

        User user = SampleStock.u1.clone();
        user.setId(loggedUserId);

        loginAsUser(user);

        String urlFromat = "/api/stock/reserv/%s/user/%s/from/%s/to/%s";
        String url = String.format(urlFromat,stockItemId, loggedUserId,hireStartDate,hireEndDate);

        mockMvc.perform(put(url)).andExpect(status().isAccepted());
    }

    @Test
    public void Should_putHireDevice_with_wrong_user_access_denied_code_403() throws Exception {
        final long loggedUserId = 1L;
        final long reqUserId = 2L;
        final long stockItemId = 1L;
        final String hireStartDate = "2017-05-19";
        final String hireEndDate = "2017-05-21";

        User user = SampleStock.u1.clone();
        user.setId(loggedUserId);

        loginAsUser(user);

        String urlFromat = "/api/stock/reserv/%s/user/%s/from/%s/to/%s";
        String url = String.format(urlFromat,stockItemId, reqUserId,hireStartDate,hireEndDate);

        mockMvc.perform(put(url)).andExpect(status().isForbidden());
    }

}
