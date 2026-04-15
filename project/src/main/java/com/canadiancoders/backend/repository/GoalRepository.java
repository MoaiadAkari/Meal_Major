package com.canadiancoders.backend.repository;

import com.canadiancoders.backend.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List; 

public interface GoalRepository extends JpaRepository<Goal, Integer> {
  @Query(value = "SELECT * FROM ref_goal ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
List<Goal> findRandomGoals();
}