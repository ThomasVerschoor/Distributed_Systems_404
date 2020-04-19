package com.ola.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Controller {
    @RequestMapping("/")
    public String sayHello(){
        return "Welcome to place Userst";
    }
    
}
