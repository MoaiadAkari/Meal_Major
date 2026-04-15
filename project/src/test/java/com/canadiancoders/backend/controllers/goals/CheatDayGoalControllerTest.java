package com.canadiancoders.backend.controllers.goals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.canadiancoders.backend.services.CheatDayGoalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import com.canadiancoders.backend.controllers.CheatDayGoalController;
import com.canadiancoders.backend.dtos.GoalDTO;
import com.canadiancoders.backend.dtos.PlanGoalResponseDTO;
import com.canadiancoders.backend.dtos.SelectGoalRequestDTO;
import com.canadiancoders.backend.dtos.UpdateGoalCompletionRequestDTO;
import com.canadiancoders.backend.goals.PlanGoal;
import com.canadiancoders.backend.goals.WeeklyPlan;
import com.canadiancoders.backend.repository.PlanGoalRepository;
import com.canadiancoders.backend.repository.WeeklyPlanRepository;

@WebMvcTest(controllers = CheatDayGoalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CheatDayGoalControllerTest {

    @MockBean
    private CheatDayGoalService cheatDayGoalService;

    @MockBean
    private WeeklyPlanRepository weeklyPlanRepository;

    @MockBean
    private PlanGoalRepository planGoalRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test 
    void getAllGoals() throws Exception {

        List<GoalDTO> mockGoals = List.of(
            new GoalDTO(1, "Test Goal 1", "This is a test goal.", 1),
            new GoalDTO(2, "Another Test Goal", "This is another test goal.", 1),
            new GoalDTO(3, "Third Test Goal", "This is the third test goal.", 1)
        );

        when(cheatDayGoalService.getAllGoals()).thenReturn(mockGoals);

        mockMvc.perform(get("/api/goals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Test Goal 1"));
    }

    @Test 
    void hasGoalsForWeek() throws Exception {

        WeeklyPlan mockPlan = new WeeklyPlan();

        List<PlanGoal> mockPlanGoals = List.of(
            new PlanGoal(),
            new PlanGoal(),
            new PlanGoal()
        );

        when(weeklyPlanRepository.findByUserFkAndStartDate(anyInt(), any())).thenReturn(Optional.of(mockPlan));
        when(planGoalRepository.findByWeeklyPlan_WeeklyPk(any())).thenReturn(mockPlanGoals);

        mockMvc.perform(get("/api/goals/exists?startDate=2026-01-01")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        //tests second scenario when there is a weekly plan but no goals

        when(weeklyPlanRepository.findByUserFkAndStartDate(anyInt(), any())).thenReturn(Optional.of(mockPlan));
        when(planGoalRepository.findByWeeklyPlan_WeeklyPk(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/goals/exists?startDate=2026-01-01")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));


        //tests third scenario when there is no weekly plan

        when(weeklyPlanRepository.findByUserFkAndStartDate(anyInt(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/goals/exists?startDate=2026-01-01")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test 
    void selectGoal() throws Exception {

        WeeklyPlan mockPlan = new WeeklyPlan();
        mockPlan.setWeeklyPk(1);

        SelectGoalRequestDTO request = new SelectGoalRequestDTO();
        request.setStartDate("2026-01-01");

        when(weeklyPlanRepository.findByUserFkAndStartDate(anyInt(), any())).thenReturn(Optional.of(mockPlan));
        when(cheatDayGoalService.selectGoal(any())).thenReturn(new PlanGoalResponseDTO(1, 1, 1, "Test Goal", 0, false, 1));

        mockMvc.perform(post("/api/goals/select")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joinPk").value(1))
                .andExpect(jsonPath("$.goalName").value("Test Goal"));

        //tests scenario when there is no weekly plan

        when(weeklyPlanRepository.findByUserFkAndStartDate(anyInt(), any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/goals/select")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Please create a weekly plan before selecting a goal."));
    }

    @Test 
    void updateCompletion() throws Exception {

        UpdateGoalCompletionRequestDTO request = new UpdateGoalCompletionRequestDTO();
        request.setJoinPk(1);

        when(cheatDayGoalService.updateCompletion(any())).thenReturn(new PlanGoalResponseDTO(1, 1, 1, "Test Goal", 0, false, 1));

        mockMvc.perform(put("/api/goals/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joinPk").value(1))
                .andExpect(jsonPath("$.goalName").value("Test Goal"));
    }

    @Test 
    void getPlanGoalById() throws Exception {

         UpdateGoalCompletionRequestDTO request = new UpdateGoalCompletionRequestDTO();
        request.setJoinPk(1);

        when(cheatDayGoalService.getPlanGoalById(any())).thenReturn(new PlanGoalResponseDTO(1, 1, 1, "Test Goal", 0, false, 1));

        mockMvc.perform(get("/api/goals/plan-goal/1")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.joinPk").value(1))
                .andExpect(jsonPath("$.goalName").value("Test Goal"));
        }

    

    
}
