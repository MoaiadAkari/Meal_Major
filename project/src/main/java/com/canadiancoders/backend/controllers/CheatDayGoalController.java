package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.GoalDTO;
import com.canadiancoders.backend.dtos.SelectGoalRequestDTO;
import com.canadiancoders.backend.dtos.UpdateGoalCompletionRequestDTO;
import com.canadiancoders.backend.goals.WeeklyPlan;
import com.canadiancoders.backend.repository.PlanGoalRepository;
import com.canadiancoders.backend.repository.WeeklyPlanRepository;
import com.canadiancoders.backend.services.CheatDayGoalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class CheatDayGoalController {

    private final CheatDayGoalService cheatDayGoalService;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final PlanGoalRepository planGoalRepository;

    public CheatDayGoalController(CheatDayGoalService cheatDayGoalService,
                                  WeeklyPlanRepository weeklyPlanRepository,
                                  PlanGoalRepository planGoalRepository) {
        this.cheatDayGoalService = cheatDayGoalService;
        this.weeklyPlanRepository = weeklyPlanRepository;
        this.planGoalRepository = planGoalRepository;
    }

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getAllGoals() {
        return ResponseEntity.ok(cheatDayGoalService.getAllGoals());
    }

    @GetMapping("/exists")
    public ResponseEntity<?> hasGoalForWeek(@RequestParam String startDate,
                                            HttpSession session) {
        try {
            Integer userId = getLoggedInUserId(session);
            LocalDate parsedDate = LocalDate.parse(startDate);

            WeeklyPlan weeklyPlan = weeklyPlanRepository
                    .findByUserFkAndStartDate(userId, parsedDate)
                    .orElse(null);

            if (weeklyPlan == null) {
                return ResponseEntity.ok(false);
            }

            boolean hasGoal = !planGoalRepository
                    .findByWeeklyPlan_WeeklyPk(weeklyPlan.getWeeklyPk())
                    .isEmpty();

            return ResponseEntity.ok(hasGoal);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid startDate format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking if a goal exists for the week.");
        }
    }

    @PostMapping("/select")
    public ResponseEntity<?> selectGoal(@RequestBody SelectGoalRequestDTO request,
                                        HttpSession session) {
        try {
            Integer userId = getLoggedInUserId(session);
            LocalDate parsedDate = LocalDate.parse(request.getStartDate());

            WeeklyPlan weeklyPlan = weeklyPlanRepository
                    .findByUserFkAndStartDate(userId, parsedDate)
                    .orElse(null);

            if (weeklyPlan == null) {
                return ResponseEntity.badRequest()
                        .body("Please create a weekly plan before selecting a goal.");
            }

            request.setWeeklyPlanId(weeklyPlan.getWeeklyPk());

            return ResponseEntity.ok(cheatDayGoalService.selectGoal(request));

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid startDate format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error selecting goal.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCompletion(@RequestBody UpdateGoalCompletionRequestDTO request,
                                              HttpSession session) {
        try {
            getLoggedInUserId(session);
            return ResponseEntity.ok(cheatDayGoalService.updateCompletion(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating goal progress.");
        }
    }

    @GetMapping("/plan-goal/{joinPk}")
    public ResponseEntity<?> getPlanGoalById(@PathVariable Integer joinPk,
                                             HttpSession session) {
        try {
            getLoggedInUserId(session);
            return ResponseEntity.ok(cheatDayGoalService.getPlanGoalById(joinPk));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching saved goal progress.");
        }
    }

    private Integer getLoggedInUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        System.out.println("userId in session = " + userIdObj);

        if (userIdObj == null) {
            throw new RuntimeException("User not logged in");
        }

        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).intValue();
        }

        throw new RuntimeException("Invalid userId type in session: " + userIdObj.getClass().getName());
    }
}