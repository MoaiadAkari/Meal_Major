package com.canadiancoders.backend.controllers;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.canadiancoders.backend.dtos.loginDTOS.Email;
import com.canadiancoders.backend.dtos.loginDTOS.ResponseMessage;
import com.canadiancoders.backend.services.EmailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/reset-request")
public class EmailController {
	
	private JdbcTemplate jdbc;
	
	private final String resetLink= "http://localhost:8080/?token=";
	
	private EmailService emailer;
	
	public EmailController(JdbcTemplate jdbc, EmailService emailer)
	{
		this.jdbc=jdbc;
		this.emailer=emailer;
	}
	
	@PostMapping
	public ResponseMessage recoveryEmail(@RequestBody Email email) {
		Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email.getEmail());

		if(count != null && count>0)
		{
			String token = UUID.randomUUID().toString();
			jdbc.update("UPDATE users SET token = ? WHERE email = ?", token, email.getEmail());
			
			try{
				emailer.sendResetEmail(email.getEmail(), (resetLink+token).toString());
			}catch(MessagingException me)
			{
				return new ResponseMessage("Email failed to send", false);
			}
					
			return new ResponseMessage("Email has been sent", true);
		
		}else
		{
			return new ResponseMessage("Email not in Database", false);
		}
		
		
	}

}
