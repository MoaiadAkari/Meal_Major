package com.canadiancoders.backend.controllers.passwordReset;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import com.canadiancoders.backend.controllers.PasswordResetController;

@WebMvcTest(controllers = PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PasswordResetControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validate() throws Exception {
        
        when( jdbc.queryForObject(startsWith("SELECT"), eq(Integer.class), any())).thenReturn(1);

        mockMvc.perform(get("/reset-password/validate?token=123456789")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(jdbc).queryForObject(
            anyString(),
            eq(Integer.class),
            captor.capture()
        );
        
        Object arg = captor.getValue();

        assertThat(arg).isEqualTo("123456789");
    }

    @Test
    void resetPassword() throws Exception {
        
        when( jdbc.queryForObject(startsWith("SELECT"), eq(Integer.class), any())).thenReturn(1);

        when( jdbc.update(startsWith("UPDATE"), any(), any())).thenReturn(1);
        when( jdbc.update(startsWith("UPDATE"), any(String.class))).thenReturn(1);

        mockMvc.perform(post("/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"123456789\", \"newPassword\":\"123456789\", \"confirm_password\":\"123456789\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));


        ArgumentCaptor<Object> captor2 = ArgumentCaptor.forClass(Object.class);

        verify(jdbc).update(
            startsWith("UPDATE users SET password"),
            captor2.capture(),
            captor2.capture()
        );

        List<Object> args = captor2.getAllValues();

        assertThat(args.get(0)).isEqualTo("123456789");
        assertThat(args.get(1)).isEqualTo("123456789"); 
    }
}
