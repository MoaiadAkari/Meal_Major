package com.canadiancoders.backend.controllers.goals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.canadiancoders.backend.services.CheatDayGoalService;
import com.canadiancoders.backend.services.CheatDayService;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import java.util.List;

import com.canadiancoders.backend.controllers.CheatDayController;
import com.canadiancoders.backend.dtos.DessertResponseDTO;
import com.canadiancoders.backend.goals.PlanGoal;
import com.canadiancoders.backend.goals.WeeklyPlan;

@WebMvcTest(controllers = CheatDayController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CheatDayControllerTest {

    @MockBean
    private CheatDayService cheatDayService;

    @MockBean
    private CheatDayGoalService cheatDayGoalService;

    @Autowired
    private MockMvc mockMvc;

    @Test 
    void getRandomDessert() throws Exception {

        DessertResponseDTO dessert = new DessertResponseDTO(1L, "Chocolate Cake", "/images/chocolate_cake.jpg", "A delicious chocolate cake recipe."); 

        when(cheatDayService.getRandomDessert()).thenReturn(dessert);

        mockMvc.perform(get("/api/cheat-day/random-dessert")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chocolate Cake"))
                .andExpect(jsonPath("$.description").value("A delicious chocolate cake recipe."));
    }

    @Test 
    void getProgress() throws Exception {

        //NO weekly plan

       when(cheatDayGoalService.getWeeklyPlanForUser(eq(1), any())).thenReturn(null);

        mockMvc.perform(get("/api/cheat-day/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.completed").value(0));

        //NO goals weekly plan
        WeeklyPlan mockPlanEmpty = new WeeklyPlan();
        mockPlanEmpty.setWeeklyPk(1);

        when(cheatDayGoalService.getWeeklyPlanForUser(eq(1), any())).thenReturn(mockPlanEmpty);
        when(cheatDayGoalService.getPlanGoalsForWeeklyPlan(eq(1))).thenReturn(List.of());

        mockMvc.perform(get("/api/cheat-day/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.completed").value(0));

        //Weekly plan with goals and 1 complete
        WeeklyPlan mockPlan = new WeeklyPlan();
        mockPlan.setWeeklyPk(1);

        PlanGoal goal1 = new PlanGoal();
        goal1.setComplete(true);

        List<PlanGoal> mockPlanGoals = List.of(
            goal1,
            new PlanGoal(),
            new PlanGoal()
        );

        when(cheatDayGoalService.getWeeklyPlanForUser(eq(1), any())).thenReturn(mockPlan);
        when(cheatDayGoalService.getPlanGoalsForWeeklyPlan(eq(1))).thenReturn(mockPlanGoals);

        mockMvc.perform(get("/api/cheat-day/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.completed").value(1));
    }


                
}

