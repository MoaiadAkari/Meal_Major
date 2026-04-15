package com.canadiancoders.backend.controllers;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.canadiancoders.backend.dtos.loginDTOS.LoginResponse;
import com.canadiancoders.backend.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	private JdbcTemplate jdbc;
	
	public LoginController(JdbcTemplate jdbc)
	{
		this.jdbc=jdbc;
	}
	
	@PostMapping
	public LoginResponse login(@RequestBody User user, HttpServletRequest request) {

		request.getSession().invalidate();
    	HttpSession session = request.getSession(true);
		System.out.println("Method called!");


		Long count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ? AND password = ?", Long.class, user.getEmail(), user.getPassword());
		
		System.out.println("Method called2!");

		if(count != null && count>0)
		{
			Long userId= jdbc.queryForObject("SELECT user_pk FROM users WHERE email = ?", Long.class, user.getEmail());
			session.setAttribute("userId", userId);
			return new LoginResponse(true, userId);
		
		}else
		{
			return new LoginResponse(false, "User doesn't exist, Signup");
		}
		
		
	}

}
