package com.canadiancoders.backend.services;

import com.canadiancoders.backend.dtos.GoalDTO;
import com.canadiancoders.backend.dtos.PlanGoalResponseDTO;
import com.canadiancoders.backend.dtos.SelectGoalRequestDTO;
import com.canadiancoders.backend.dtos.UpdateGoalCompletionRequestDTO;
import com.canadiancoders.backend.goals.Goal;
import com.canadiancoders.backend.goals.PlanGoal;
import com.canadiancoders.backend.goals.WeeklyPlan;
import com.canadiancoders.backend.repository.GoalRepository;
import com.canadiancoders.backend.repository.PlanGoalRepository;
import com.canadiancoders.backend.repository.WeeklyPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheatDayGoalService {

    private final GoalRepository goalRepository;
    private final PlanGoalRepository planGoalRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;

    public CheatDayGoalService(GoalRepository goalRepository,
                               PlanGoalRepository planGoalRepository,
                               WeeklyPlanRepository weeklyPlanRepository) {
        this.goalRepository = goalRepository;
        this.planGoalRepository = planGoalRepository;
        this.weeklyPlanRepository = weeklyPlanRepository;
    }

    // get all goals
    public List<GoalDTO> getAllGoals() {
        return goalRepository.findAll()
                .stream()
                .map(goal -> new GoalDTO(
                        goal.getGoalPk(),
                        goal.getGoalName(),
                        null,
                        goal.getRequiredIterations() // pass new field
                ))
                .toList();
    }

    // get 3 random goals
    public List<GoalDTO> getRandomGoals() {
        return goalRepository.findRandomGoals()
                .stream()
                .limit(3)
                .map(goal -> new GoalDTO(
                        goal.getGoalPk(),
                        goal.getGoalName(),
                        null,
                        goal.getRequiredIterations() // pass new field
                ))
                .toList();
    }

    // get a specific plan goal
    public PlanGoalResponseDTO getPlanGoalById(Integer joinPk) {
        PlanGoal planGoal = planGoalRepository.findById(joinPk)
                .orElseThrow(() -> new RuntimeException("Plan goal not found"));

        return new PlanGoalResponseDTO(
                planGoal.getJoinPk(),
                planGoal.getWeeklyPlan().getWeeklyPk(),
                planGoal.getGoal().getGoalPk(),
                planGoal.getGoal().getGoalName(),
                planGoal.getProgressCount(),
                planGoal.getComplete(),
                planGoal.getGoal().getRequiredIterations() // new field
        );
    }

    // select or replace a goal for a weekly plan
    public PlanGoalResponseDTO selectGoal(SelectGoalRequestDTO request) {
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(request.getWeeklyPlanId())
                .orElseThrow(() -> new RuntimeException("Weekly plan not found"));

        List<PlanGoal> existingPlanGoals = planGoalRepository
                .findByWeeklyPlan_WeeklyPk(weeklyPlan.getWeeklyPk());

        PlanGoal planGoal;

        // if already exists, overwrite it
        if (!existingPlanGoals.isEmpty()) {
            planGoal = existingPlanGoals.get(0);
            planGoal.setGoal(goal);
            planGoal.setComplete(false);
            planGoal.setProgressCount(0);
        } else {
            // otherwise create new one
            planGoal = new PlanGoal();
            planGoal.setWeeklyPlan(weeklyPlan);
            planGoal.setGoal(goal);
            planGoal.setComplete(false);
            planGoal.setProgressCount(0);
        }

        PlanGoal saved = planGoalRepository.save(planGoal);

        return new PlanGoalResponseDTO(
                saved.getJoinPk(),
                saved.getWeeklyPlan().getWeeklyPk(),
                saved.getGoal().getGoalPk(),
                saved.getGoal().getGoalName(),
                saved.getProgressCount(),
                saved.getComplete(),
                saved.getGoal().getRequiredIterations()
        );
    }

    // update progress when user clicks "complete"
    public PlanGoalResponseDTO updateCompletion(UpdateGoalCompletionRequestDTO request) {
        PlanGoal planGoal = planGoalRepository.findById(request.getJoinPk())
                .orElseThrow(() -> new RuntimeException("Plan goal not found"));

        Integer currentProgress = planGoal.getProgressCount();
        if (currentProgress == null) {
            currentProgress = 0;
        }

        // get required iterations from DB instead of hardcoding 3
        Integer requiredIterations = planGoal.getGoal().getRequiredIterations();

        

        int newProgress = currentProgress + 1;

        // cap progress at required value
        if (newProgress > requiredIterations) {
            newProgress = requiredIterations;
        }

        planGoal.setProgressCount(newProgress);

        // complete only when target is reached
        if (newProgress >= requiredIterations) {
            planGoal.setComplete(true);
        } else {
            planGoal.setComplete(false);
        }

        PlanGoal saved = planGoalRepository.save(planGoal);

        return new PlanGoalResponseDTO(
                saved.getJoinPk(),
                saved.getWeeklyPlan().getWeeklyPk(),
                saved.getGoal().getGoalPk(),
                saved.getGoal().getGoalName(),
                saved.getProgressCount(),
                saved.getComplete(),
                saved.getGoal().getRequiredIterations(),
                "Progress updated successfully"
        );
    }

    // get selected goal for a specific week
    public Integer getSelectedGoalId(Long userId, LocalDate startDate) {

        WeeklyPlan weeklyPlan = weeklyPlanRepository
                .findByUserFkAndStartDate(userId.intValue(), startDate)
                .orElse(null);

        if (weeklyPlan == null) {
            return null;
        }

        List<PlanGoal> planGoals = planGoalRepository
                .findByWeeklyPlan_WeeklyPk(weeklyPlan.getWeeklyPk());

        if (planGoals.isEmpty()) {
            return null;
        }

        // only one goal per week
        return planGoals.get(0).getGoal().getGoalPk();
    }

    // helper methods (used elsewhere)

    public WeeklyPlan getWeeklyPlanForUser(int userId, LocalDate startDate) {
        return weeklyPlanRepository.findByUserFkAndStartDate(userId, startDate).orElse(null);
    }

    public List<PlanGoal> getPlanGoalsForWeeklyPlan(int weeklyPk) {
        return planGoalRepository.findByWeeklyPlan_WeeklyPk(weeklyPk);
    }
}