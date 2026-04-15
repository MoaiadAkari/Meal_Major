package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.canadiancoders.backend.dtos.loginDTOS.InformedToken;
import com.canadiancoders.backend.dtos.loginDTOS.SignupRequest;
import com.canadiancoders.backend.repository.UserRepository;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;

@RestController
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<InformedToken> signup(@RequestBody SignupRequest req, HttpServletRequest request) {

    request.getSession().invalidate();
    HttpSession session = request.getSession(true);
        // Basic checks
     if (req.getEmail() == null 
                || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(new InformedToken("Missing info", "", false, null));
        }

       /*  // Sprint requirement: verify passwords match
        if (!req.password.equals(req.confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }*/

        // Prevent duplicate accounts
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(new InformedToken("Account already exists", "", false, null));
        }

        // Hash the password before saving
        //String hash = sha256(req.password);

        // Save user to DB
        //User user = new User(req.getEmail(), req.getPassword(), req.getFirstName(), req.getLastName(), java.sql.Date.valueOf(req.getDob()), req.getSex());
        
User user = new User(req.getFirstName() + " " + req.getLastName(), req.getEmail(), req.getPassword(), java.sql.Date.valueOf(req.getDob()), req.getSex());

        String token = UUID.randomUUID().toString();
        user.setToken(token);

        userRepository.save(user);

        Long userId = null;

        try {
            User createdUser = userRepository.findByEmail(req.getEmail()).orElseThrow(()->new Exception("User doesn't exist"));
            userId = createdUser.getUserPk();
		    session.setAttribute("userId", userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(new InformedToken("DB error", "", false, null));
        }

        return ResponseEntity.ok(new InformedToken("Signup successful", token, true, userId));
    }

   /*  private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }*/
}