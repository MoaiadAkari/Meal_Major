package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.AddRecipeRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpSession;




@RestController
@RequestMapping("/api/weekly-plan")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class WeeklyPlanController {

    @Autowired
    private JdbcTemplate jdbc;

    private final List<String> validDays =
            List.of("mon","tue","wed","thu","fri","sat","sun");

    private final List<String> validMeals =
            List.of("breakfast","morning","lunch","afternoon","dinner","night");


    @PostMapping("/add")
    public ResponseEntity<?> addRecipeToPlan(
            @RequestBody AddRecipeRequestDTO body,
            HttpSession session) {

        Number userPkNum = (Number) session.getAttribute("userId");
        Integer userPk = userPkNum.intValue();

        String day = body.getDay();
        String meal = body.getMeal();
        Integer recipeId = body.getRecipeId();

        if (!validDays.contains(day) || !validMeals.contains(meal)) {
            return ResponseEntity.badRequest().body("Invalid day or meal");
        }

        LocalDate weekStart = LocalDate.parse(body.getWeekStart());
        LocalDate weekEnd = weekStart.plusDays(6);

        Number weeklyPkNum = jdbc.query(
                "SELECT weekly_pk FROM weekly_plan WHERE user_fk=? AND start_date=?",
                rs -> rs.next() ? (Number) rs.getObject("weekly_pk") : null,
                userPk, weekStart
        );
        Integer weeklyPk = (weeklyPkNum != null) ? weeklyPkNum.intValue() : null;

        if (weeklyPk == null) {
            jdbc.update(
                "INSERT INTO weekly_plan (user_fk, start_date, end_date) VALUES (?,?,?)",
                userPk, weekStart, weekEnd
            );

            Number newWeeklyPkNum = jdbc.queryForObject(
                "SELECT weekly_pk FROM weekly_plan WHERE user_fk=? AND start_date=?",
                Number.class, userPk, weekStart
            );
            weeklyPk = (newWeeklyPkNum != null) ? newWeeklyPkNum.intValue() : null;
        }

        Number dailyPkNum = jdbc.query(
                "SELECT " + day + " FROM weekly_plan WHERE weekly_pk=?",
                rs -> rs.next() ? (Number) rs.getObject(1) : null,
                weeklyPk
        );
        Integer dailyPk = (dailyPkNum != null) ? dailyPkNum.intValue() : null;

        if (dailyPk == null) {
            jdbc.update("INSERT INTO daily_recipe DEFAULT VALUES");

            Number newDailyPkNum = jdbc.queryForObject(
                    "SELECT currval(pg_get_serial_sequence('daily_recipe','daily_pk'))",
                    Number.class
            );
            dailyPk = (newDailyPkNum != null) ? newDailyPkNum.intValue() : null;

            jdbc.update(
                "UPDATE weekly_plan SET " + day + "=? WHERE weekly_pk=?",
                dailyPk, weeklyPk
            );
        }

        jdbc.update(
            "UPDATE daily_recipe SET " + meal + "_fk=? WHERE daily_pk=?",
            recipeId, dailyPk
        );

        return ResponseEntity.ok(Map.of("status","added"));
    }

    // ----------------------------
    // GET WEEKLY PLAN
    // ----------------------------

    @GetMapping("/week")
    public ResponseEntity<?> getWeekPlan(
            @RequestParam String start,
            HttpSession session) {
        
        Number userPkNum = (Number) session.getAttribute("userId");
        Integer userPk = userPkNum.intValue();

        LocalDate startDate = LocalDate.parse(start);

        List<Map<String,Object>> weekly =
                jdbc.queryForList("""
                SELECT *
                FROM weekly_plan
                WHERE user_fk=? AND start_date=?
        """, userPk, startDate);

        if (weekly.isEmpty()) {
            return ResponseEntity.ok(Map.of());
        }

        Map<String,Object> week = weekly.get(0);

        Map<String,Object> result = new HashMap<>();

        for (String day : validDays) {

            Number dailyPkNum = (Number) week.get(day);

            Integer dailyPk = (dailyPkNum != null) ? dailyPkNum.intValue() : null;

            if (dailyPk == null) continue;

            List<Map<String,Object>> meals =
                    jdbc.queryForList("""
                        SELECT  breakfast_fk,
                                morning_fk,
                                lunch_fk,
                                afternoon_fk,
                                dinner_fk,
                                night_fk
                        FROM daily_recipe
                        WHERE daily_pk=?
                    """, dailyPk);

            Map<String,Object> mealMap = meals.get(0);

            mealMap.replaceAll((k, v) -> v == null ? null : ((Number)v).intValue());

            result.put(day, mealMap);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeRecipe(
            @RequestParam String day,
            @RequestParam String meal,
            @RequestParam String start,
            HttpSession session) {

        Number userPkNum = (Number) session.getAttribute("userId");
        Integer userPk = userPkNum.intValue();

        LocalDate startDate = LocalDate.parse(start);

        Integer weeklyPk = jdbc.queryForObject(
            "SELECT weekly_pk FROM weekly_plan WHERE user_fk=? AND start_date=?",
            Integer.class, userPk, startDate
        );

        Integer dailyPk = jdbc.queryForObject(
            "SELECT " + day + " FROM weekly_plan WHERE weekly_pk=?",
            Integer.class, weeklyPk
        );

        jdbc.update(
            "UPDATE daily_recipe SET " + meal + "_fk=NULL WHERE daily_pk=?",
            dailyPk
        );

        return ResponseEntity.ok(Map.of("status","removed"));
    }
}