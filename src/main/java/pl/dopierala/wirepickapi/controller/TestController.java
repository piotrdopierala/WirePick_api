package pl.dopierala.wirepickapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "dziala";
    }

    @GetMapping("/secret")
    public String secret(){
        return "secret";
    }

    @GetMapping ("/test/user/{userId}")
    public String userAccessTest(@PathVariable Integer userId) {
        return "accessed by user with id="+userId.toString();
    }
}
