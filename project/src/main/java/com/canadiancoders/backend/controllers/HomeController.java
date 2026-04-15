package com.canadiancoders.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@RequestParam(required = false) String token) {
        if(token == null)
        {
            return "redirect:/html/login.html";
        }else
        {
            return "redirect:/html/login.html/?token=" + token;
        }
    }
}