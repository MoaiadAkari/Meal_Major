package com.canadiancoders.backend.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canadiancoders.backend.dtos.loginDTOS.ResetPasswordRequest;
import com.canadiancoders.backend.dtos.loginDTOS.ResponseMessage;
import com.canadiancoders.backend.dtos.loginDTOS.Validation;

@RestController
@RequestMapping("/reset-password")
public class PasswordResetController {
	
	private JdbcTemplate jdbc;

	public PasswordResetController(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	@GetMapping("/validate")
	public Validation validateToken(@RequestParam String token)
	{
        System.out.println(token);
		Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE token = ?", Integer.class, token);
		
		if(count != null && count>0)
		{
			//Removed: jdbc.update("UPDATE users SET token = null WHERE token = ?", token);	//deletes token so it can't be reused to reset the password again
			return new Validation(true);
		}else
			return new Validation(false);
		
	}

    @PostMapping
    //Removed: public ResponseMessage resetPassword(@RequestBody com.canadiancoders.backend.dtos.ResetPasswordRequest request)
    public ResponseMessage resetPassword(@RequestBody ResetPasswordRequest request) //Added

    {

        String token = request.getToken();
        String newPassword = request.getNewPassword();

        //Check token exists
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE token = ?",
                Integer.class,
                token
        );

        if (count == null || count == 0) {
            return new ResponseMessage("Invalid token");
        }

        //Update password
        jdbc.update(
                "UPDATE users SET password = ? WHERE token = ?",
                newPassword,
                token
        );

        //Now delete token so it can't be reused
        jdbc.update(
                "UPDATE users SET token = null WHERE token = ?",
                token
        );

        return new ResponseMessage("Password updated successfully");
    }


}
