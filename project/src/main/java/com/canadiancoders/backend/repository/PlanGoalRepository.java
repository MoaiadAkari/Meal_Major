package com.canadiancoders.backend.repository;

import com.canadiancoders.backend.goals.PlanGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanGoalRepository extends JpaRepository<PlanGoal, Integer> {
    List<PlanGoal> findByWeeklyPlan_WeeklyPk(Integer weeklyPk);
    Optional<PlanGoal> findByWeeklyPlan_WeeklyPkAndGoal_GoalPk(Integer weeklyPk, Integer goalPk);

}