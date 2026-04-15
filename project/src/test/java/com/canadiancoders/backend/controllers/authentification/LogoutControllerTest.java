package com.canadiancoders.backend.controllers.authentification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.canadiancoders.backend.controllers.LogoutController;

@WebMvcTest(controllers = LogoutController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LogoutControllerTest 
{
    @Autowired
    private MockMvc mockMvc;

    @Test
    void logout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(post("/logOut")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("JSESSIONID", 0));

        assertThat(session.isInvalid()).isTrue();
    }
}
