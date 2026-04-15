package com.canadiancoders.backend.controllers.authentification;

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

import static org.assertj.core.api.Assertions.assertThat;

import com.canadiancoders.backend.controllers.LoginController;

@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception {
        
        when( jdbc.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(1L);
        when( jdbc.queryForObject(anyString(), eq(Long.class), any())).thenReturn(1L);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@email.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(request().sessionAttribute("userId", 1L));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(jdbc).queryForObject(
            anyString(),
            eq(Long.class),
            captor.capture(),
            captor.capture()
        );

        List<Object> args = captor.getAllValues();

        assertThat(args.get(0)).isEqualTo("test@email.com");
        assertThat(args.get(1)).isEqualTo("password");

    }
}
