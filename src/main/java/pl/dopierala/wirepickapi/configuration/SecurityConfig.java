package pl.dopierala.wirepickapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.service.UserService;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userDetailsService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/secret")
                .hasRole("USER")
                .antMatchers("/test")
                .permitAll()
                .and()
                .csrf().disable()
                .formLogin().permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initDefaultUser(){
        if(userDetailsService.isUserExists("admin")){
            return;
        }else{
            User adminUser = User.builder()
                    .withFirstName("admin")
                    .withLastName("admin")
                    .withLogin("admin")
                    .withPassword(bCryptPasswordEncoder.encode("admin"))
                    .build();
            userDetailsService.saveUser(adminUser);
        }
    }
}
