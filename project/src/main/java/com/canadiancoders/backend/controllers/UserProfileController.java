package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.repository.UserRepository;
import com.canadiancoders.backend.user.User;
import com.canadiancoders.backend.dtos.UserDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===============================
    // GET CURRENT USER
    // ===============================
    @GetMapping("/me")
    public UserDTO getCurrentUser(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not logged in"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

    return convertToDTO(user);
}

////////////////////////////////////////////////////////////////////////////////////////////

    // ===============================
    // UPDATE CURRENT USER
    // ===============================
    @PutMapping("/me")
    public UserDTO updateCurrentUser(
            HttpSession session,
            @RequestBody UserDTO updatedData) {

        // System.out.println("PUT /users/me called with: " + updatedData);

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not logged in"
            );
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Update only allowed fields
        existingUser.setName(updatedData.getName());
        existingUser.setFoodAllergies(updatedData.getFoodAllergies());
        existingUser.setIntolerances(updatedData.getIntolerances());
        existingUser.setPreferences(updatedData.getPreferences());

        userRepository.save(existingUser);

        return convertToDTO(existingUser);
    }

    // ===============================
    // CONVERTER METHOD
    // ===============================
    private UserDTO convertToDTO(User user) {

        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setFoodAllergies(user.getFoodAllergies());
        dto.setIntolerances(user.getIntolerances());
        dto.setPreferences(user.getPreferences());

        return dto;
    }
}