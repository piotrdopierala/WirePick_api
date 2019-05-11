package pl.dopierala.wirepickapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "dziala";
    }

    @GetMapping("/secret")
    public String sectet(){
        return "secret";
    }
}
