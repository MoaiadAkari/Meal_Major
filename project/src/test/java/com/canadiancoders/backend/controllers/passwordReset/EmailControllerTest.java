package com.canadiancoders.backend.controllers.passwordReset;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.canadiancoders.backend.controllers.EmailController;
import com.canadiancoders.backend.services.EmailService;

@WebMvcTest(controllers = EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmailControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @MockBean
    private EmailService emailer;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recoveryEmail() throws Exception {
        
        when( jdbc.queryForObject(startsWith("SELECT"), eq(Integer.class), any())).thenReturn(1);
        when( jdbc.update(startsWith("UPDATE"), any(), any())).thenReturn(1);

        doNothing().when(emailer).sendResetEmail(anyString(), anyString());

        mockMvc.perform(post("/reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email has been sent"))
                .andExpect(jsonPath("$.success").value(true));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(emailer).sendResetEmail(
            captor.capture(),
            captor.capture()
        );

        List<String> args = captor.getAllValues();

        assertThat(args.get(0)).isEqualTo("test@email.com");
        String url = args.get(1);
        assertThat(url).startsWith("http://localhost:8080/?token=");
        
        String token = url.substring("http://localhost:8080/?token=".length());

        assertDoesNotThrow(() -> UUID.fromString(token));
    }
}
