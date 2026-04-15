package com.canadiancoders.backend.controllers.authentification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import com.canadiancoders.backend.controllers.AuthController;
import com.canadiancoders.backend.repository.UserRepository;
import com.canadiancoders.backend.user.User;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void signup() throws Exception {

        User mockUser = new User();
        Field idField = User.class.getDeclaredField("user_pk");
        idField.setAccessible(true);
        idField.set(mockUser, 1L);

        when( userRepository.existsByEmail(anyString())).thenReturn(false);
        when( userRepository.save(any())).thenReturn(new User());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@email.com\",\"password\":\"password\", \"firstName\":\"John\", \"lastName\":\"Doe\", \"dob\":\"1990-01-01\", \"sex\":\"M\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Signup successful"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(request().sessionAttribute("userId", 1L));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(
            captor.capture()
        );

        User arg = captor.getValue();

        assertThat(arg.getEmail()).isEqualTo("test@email.com");
        assertThat(arg.getName()).isEqualTo("John Doe");
        assertThat(arg.getPassword()).isEqualTo("password");
        assertThat(arg.getDateOfBirth()).isEqualTo("1990-01-01");
        assertThat(arg.getSex()).isEqualTo("M");
        assertDoesNotThrow(() -> UUID.fromString(arg.getToken()));
    }
}
