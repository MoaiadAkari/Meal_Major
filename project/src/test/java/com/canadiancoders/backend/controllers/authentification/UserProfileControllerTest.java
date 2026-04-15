package com.canadiancoders.backend.controllers.authentification;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.canadiancoders.backend.controllers.UserProfileController;
import com.canadiancoders.backend.dtos.UserDTO;
import com.canadiancoders.backend.repository.UserRepository;
import com.canadiancoders.backend.user.User;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserProfileControllerTest 
{

    @MockBean
    private UserRepository userRepo;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCurrentUser() throws Exception {

        List<String> mockAllergies = List.of("testval", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("testval", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("testval", "Kosher", "Vegetarian");
        
        User mockUser = new User("testuser", "testuser@test.com", "testpassword", Date.valueOf("1990-01-01"), "Male");
        mockUser.setFoodAllergies(mockAllergies);
        mockUser.setIntolerances(mockHealth);
        mockUser.setPreferences(mockBelief);

        when(userRepo.findById(eq(1L))).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@test.com"))
                .andExpect(jsonPath("$.foodAllergies[0]").value("testval"));
        
    }

    @Test
    void updateCurrentUser() throws Exception {

        List<String> mockAllergies = List.of("testval", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("testval", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("testval", "Kosher", "Vegetarian");

       
        
        User mockUser = new User("testuser", "testuser@test.com", "testpassword", Date.valueOf("1990-01-01"), "Male");
        mockUser.setFoodAllergies(mockAllergies);
        mockUser.setIntolerances(mockHealth);
        mockUser.setPreferences(mockBelief);

        // Tests changing name, allergies
        UserDTO updatedData = convertToDTO(mockUser);
        updatedData.setName("newName");
        List<String> newAllergies = List.of("newval", "Shellfish", "Dairy");
        updatedData.setFoodAllergies(newAllergies);

        when(userRepo.findById(eq(1L))).thenReturn(Optional.of(mockUser));
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData))
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.email").value("testuser@test.com"))
                .andExpect(jsonPath("$.foodAllergies[0]").value("newval"));
        
    }

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

