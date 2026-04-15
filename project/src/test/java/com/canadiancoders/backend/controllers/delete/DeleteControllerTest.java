package com.canadiancoders.backend.controllers.delete;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.canadiancoders.backend.controllers.DeleteController;

@WebMvcTest(controllers = DeleteController.class)
@AutoConfigureMockMvc(addFilters = false)   // Disable security filters for testing (generally needed to make sure the test passes independently of rest of app)
public class DeleteControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception {
        
        when( jdbc.queryForObject(startsWith("SELECT"), eq(String.class), any())).thenReturn("test recipe");
        when( jdbc.update(startsWith("DELETE"), any(Object.class))).thenReturn(1);

        mockMvc.perform(delete("/recipes/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted: test recipe"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(jdbc).queryForObject(
            anyString(),
            eq(String.class),
            captor.capture()
        );

        Object arg = captor.getValue();

        assertThat(arg).isEqualTo(1);

    }
}
