package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.DessertResponseDTO;
import com.canadiancoders.backend.dtos.ProgressResponseDTO;
import com.canadiancoders.backend.services.CheatDayGoalService;
import com.canadiancoders.backend.services.CheatDayService;
import com.canadiancoders.backend.goals.PlanGoal;
import com.canadiancoders.backend.goals.WeeklyPlan;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cheat-day")
public class CheatDayController {

    private final CheatDayService cheatDayService;
    private final CheatDayGoalService cheatDayGoalService;

    public CheatDayController(CheatDayService cheatDayService,
                              CheatDayGoalService cheatDayGoalService) {
        this.cheatDayService = cheatDayService;
        this.cheatDayGoalService = cheatDayGoalService;
    }

    // GET RANDOM DESSERT

    @GetMapping("/random-dessert")
    public DessertResponseDTO getRandomDessert() {
        return cheatDayService.getRandomDessert();
    }


    // GET PROGRESS
 
    @GetMapping("/progress")
    public ProgressResponseDTO getProgress(HttpSession session) {
        //  Get user id from session
        Number userIdNum = (Number) session.getAttribute("userId");
        if (userIdNum == null) {
            throw new RuntimeException("User not logged in");
        }
        int userId = userIdNum.intValue();

        // Calculate current week start (Monday)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        // Fetch WeeklyPlan for user
        WeeklyPlan weeklyPlan = cheatDayGoalService.getWeeklyPlanForUser(userId, weekStart);
        if (weeklyPlan == null) {
            return new ProgressResponseDTO(0, 0); // no goals yet
        }

        // Fetch PlanGoals for that weekly plan
        List<PlanGoal> planGoals = cheatDayGoalService.getPlanGoalsForWeeklyPlan(weeklyPlan.getWeeklyPk());
        if (planGoals.isEmpty()) {
            return new ProgressResponseDTO(0, 0); // no goals selected
        }

        // Calculate completed vs total
        int total = planGoals.size();
        int completed = (int) planGoals.stream()
                .filter(g -> g.getComplete() != null && g.getComplete())
                .count();

        // Return progress DTO
        return new ProgressResponseDTO(total, completed);
    }
}