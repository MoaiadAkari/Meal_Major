package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.GoalDTO;
import com.canadiancoders.backend.services.CheatDayGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin
public class GoalRandomController {

    private final CheatDayGoalService cheatDayGoalService;

    public GoalRandomController(CheatDayGoalService cheatDayGoalService) {
        this.cheatDayGoalService = cheatDayGoalService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<GoalDTO>> getRandomGoals() {
        return ResponseEntity.ok(cheatDayGoalService.getRandomGoals());
    }
}