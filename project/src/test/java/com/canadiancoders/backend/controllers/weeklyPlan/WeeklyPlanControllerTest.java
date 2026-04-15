package com.canadiancoders.backend.controllers.weeklyPlan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.canadiancoders.backend.controllers.WeeklyPlanController;
import com.canadiancoders.backend.dtos.AddRecipeRequestDTO;

@WebMvcTest(controllers = WeeklyPlanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WeeklyPlanControllerTest {

    @MockBean
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test 
    void addRecipeToPlan() throws Exception {

        //Weekly plan and day recipe doesn't exist test

        AddRecipeRequestDTO body = new AddRecipeRequestDTO();
        body.setDay("mon");
        body.setMeal("breakfast");
        body.setRecipeId(1);
        body.setWeekStart("2026-01-01");

        when(jdbc.query( anyString(), any(ResultSetExtractor.class), anyInt(), any() )).thenReturn(null);

        //Extra weekly plan creation cause null weekly plan
        when(jdbc.update(anyString(), anyInt(), any(), any())).thenReturn(1);
        when(jdbc.queryForObject(anyString(), eq(Number.class), any(), any())).thenReturn(1);

        when(jdbc.query(anyString(), any(ResultSetExtractor.class), anyInt(), any())).thenReturn(null);

        //Extra daily recipe creation cause null daily recipe
        when(jdbc.update(anyString())).thenReturn(1);
        when(jdbc.queryForObject(anyString(), eq(Number.class))).thenReturn(1);
        when(jdbc.update(contains("weekly_plan SET"), anyInt(), anyInt())).thenReturn(1);

        when(jdbc.update(contains("daily_recipe SET"), anyInt(), anyInt())).thenReturn(1);

        mockMvc.perform(post("/api/weekly-plan/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("added"));

        //not testing cases where they exist because it just skips code which I tested when everythig is done above (only change when not needed it null return value on the jdbc queries)
    }

    @Test 
    void getWeekPlan() throws Exception {

        //Empty weekly plan test case

        List<Map<String,Object>> weeklyEmpty = List.of(Map.of());
                
        when(jdbc.queryForList(contains("FROM weekly_plan"), anyInt(), any())).thenReturn(weeklyEmpty);

        mockMvc.perform(get("/api/weekly-plan/week?start=2026-01-01")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        //tests second scenario when there is a weekly plan

        //simulates weekly plan with only monday having being created
        Map<String,Object> week = new HashMap<>();
        week.put("mon", 1);
        week.put("tue", 2);
        week.put("wed", null);
        week.put("thu", null);
        week.put("fri", null);
        week.put("sat", null);
        week.put("sun", null);
        List<Map<String,Object>> weekly = List.of(week);
        when(jdbc.queryForList(contains("FROM weekly_plan"), anyInt(), any())).thenReturn(weekly);
             
        
        //simulates monday and tuesday meals being only breakfast 
        Map<String,Object> mealsMon = new HashMap<>();
        mealsMon.put("breakfast_fk", 1);
        mealsMon.put("morning_fk", null);
        mealsMon.put("lunch_fk", null);
        mealsMon.put("afternoon_fk", null);
        mealsMon.put("dinner_fk", null);
        mealsMon.put("night_fk", null);

        List<Map<String,Object>> mealsMonday = List.of(mealsMon);
        when(jdbc.queryForList(contains("breakfast_fk,"), eq(1))).thenReturn(mealsMonday);
        Map<String,Object> mealsTue = new HashMap<>();
        mealsTue.put("breakfast_fk", 2);
        mealsTue.put("morning_fk", null);
        mealsTue.put("lunch_fk", null);
        mealsTue.put("afternoon_fk", null);
        mealsTue.put("dinner_fk", null);
        mealsTue.put("night_fk", null);

        List<Map<String,Object>> mealsTuesday = List.of(mealsTue);
        when(jdbc.queryForList(contains("breakfast_fk"), eq(2))).thenReturn(mealsTuesday);

        mockMvc.perform(get("/api/weekly-plan/week?start=2026-01-01")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mon.breakfast_fk").value(1))
                .andExpect(jsonPath("$.mon.lunch_fk").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tue.breakfast_fk").value(2));
    }

    @Test 
    void removeRecipe() throws Exception {

        when(jdbc.queryForObject(contains("weekly_pk FROM weekly_plan"), eq(Integer.class), eq(1), eq(LocalDate.of(2026, 1, 1)))).thenReturn(1);

        when(jdbc.queryForObject(contains("FROM weekly_plan WHERE weekly_pk=?"), eq(Integer.class), eq(1))).thenReturn(1);

        when(jdbc.update(contains("UPDATE daily_recipe"), eq(1))).thenReturn(1);

        mockMvc.perform(delete("/api/weekly-plan/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .param("day", "mon")
                .param("meal", "breakfast")
                .param("start", "2026-01-01")
                .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("removed"));
    }
}

