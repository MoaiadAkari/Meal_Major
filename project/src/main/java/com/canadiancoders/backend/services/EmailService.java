package com.canadiancoders.backend.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private final JavaMailSender mailSender;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void sendResetEmail(String to, String resetLink) throws MessagingException
	{
		MimeMessage mime = mailSender.createMimeMessage();
		MimeMessageHelper mimeHelper = new MimeMessageHelper(mime, true);
		
		mimeHelper.setTo(to);
		mimeHelper.setSubject("Password Reset");
		mimeHelper.setText("<p> Click the following link to reset your password!</p>"+
				"<a href=\"" + resetLink +"\"> Reset Password</a>", true);
		
		mailSender.send(mime);
	}
	
}

