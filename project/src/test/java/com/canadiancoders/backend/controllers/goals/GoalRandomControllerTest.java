package com.canadiancoders.backend.controllers.goals;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.canadiancoders.backend.services.CheatDayGoalService;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import java.util.List;
import com.canadiancoders.backend.controllers.GoalRandomController;
import com.canadiancoders.backend.dtos.GoalDTO;

@WebMvcTest(controllers = GoalRandomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GoalRandomControllerTest {

    @MockBean
    private CheatDayGoalService cheatDayGoalService;

    @Autowired
    private MockMvc mockMvc;

    @Test 
    void getRandomGoals() throws Exception {

        List<GoalDTO> mockGoals = List.of(
            new GoalDTO(1, "Test Goal 1", "This is a test goal.", 1),
            new GoalDTO(2, "Another Test Goal", "This is another test goal.", 1),
            new GoalDTO(3, "Third Test Goal", "This is the third test goal.", 1)
        );

        when(cheatDayGoalService.getRandomGoals()).thenReturn(mockGoals);

        mockMvc.perform(get("/api/goals/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Test Goal 1"));
    }
}
