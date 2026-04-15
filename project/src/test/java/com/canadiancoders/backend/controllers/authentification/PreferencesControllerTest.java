package com.canadiancoders.backend.controllers.authentification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import com.canadiancoders.backend.controllers.PreferencesController;

@WebMvcTest(controllers = PreferencesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PreferencesControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveUserData() throws Exception {
        
        when(jdbc.queryForObject(startsWith("SELECT") ,eq(Long.class), any())).thenReturn(1L);

        when( jdbc.batchUpdate(startsWith("INSERT INTO user_diet"), any(), anyInt(), any())).thenReturn(new int[][] {{1}});
        when( jdbc.batchUpdate(startsWith("INSERT INTO user_allergies"), any(), anyInt(), any())).thenReturn(new int[][] {{1}});
        when( jdbc.batchUpdate(startsWith("INSERT INTO user_intolerances"), any(), anyInt(), any())).thenReturn(new int[][] {{1}});

        List<String> mockAllergies = List.of("testval", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("testval", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("testval", "Kosher", "Vegetarian");

        String jsonAlleries = objectMapper.writeValueAsString(mockAllergies);
        String jsonHealth = objectMapper.writeValueAsString(mockHealth);
        String jsonBeliefs = objectMapper.writeValueAsString(mockBelief);

        mockMvc.perform(post("/userPreferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"123456789\",\"preferences\" : { \"allergies\":"+jsonAlleries+",\"dietPreferences\":"+jsonBeliefs+", \"intolerances\":"+jsonHealth+"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.token").value(""))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(1L));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(jdbc).queryForObject(
            anyString(),
            eq(Long.class),
            captor.capture()
        );

        String token = captor.getValue();
        assertThat(token).isEqualTo("123456789");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> arrCaptor = ArgumentCaptor.forClass(List.class);

        verify(jdbc, times(3)).batchUpdate(
            anyString(),
            arrCaptor.capture(),
            anyInt(),
            any()
        );

        List<String> prefs = arrCaptor.getValue();
        assertThat(prefs.size()).isEqualTo(3);
        assertThat(prefs.get(0)).isEqualTo("testval"); 
        
    }
}
