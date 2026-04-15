package com.canadiancoders.backend.repository;

import com.canadiancoders.backend.goals.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;


public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Integer> {
    Optional<WeeklyPlan> findByUserFkAndStartDate(Integer userFk, LocalDate startDate);
}