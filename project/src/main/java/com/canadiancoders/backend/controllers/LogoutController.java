package com.canadiancoders.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/logOut")
public class LogoutController {

    public LogoutController() {}

    @PostMapping
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("Logout called");
        request.getSession().invalidate();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().body("Logged out successfully");
    }
    


}
