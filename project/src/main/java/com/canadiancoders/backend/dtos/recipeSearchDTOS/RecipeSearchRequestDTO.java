package com.canadiancoders.backend.dtos.recipeSearchDTOS;

import java.time.LocalDate;
import java.util.List;

public class RecipeSearchRequestDTO {

// initializing variables 

    private String name;
    private String difficulty;
    private Double maxCost; // cost and time are represented as an Integer and Double for search functionality (if frontend did not provide cost/time it will be set to null instead of 0)   
    private Integer maxTime; 
    private List<String> preferences;
    private Boolean excludePlanned;
    private Long userId;
    private LocalDate weekStart;

    public RecipeSearchRequestDTO() {
    }

    public RecipeSearchRequestDTO(String name, String difficulty, Double maxCost, Integer maxTime,
            List<String> preferences, Boolean excludePlanned, Long userId, LocalDate weekStart) {
        this.name = name;
        this.difficulty = difficulty;
        this.maxCost = maxCost;
        this.maxTime = maxTime;
        this.preferences = preferences;
        this.excludePlanned = excludePlanned;
        this.userId = userId;
        this.weekStart = weekStart;
    }

    @Override
    public String toString() {
        return "RecipeSearchRequestDTO [name=" + name + ", difficulty=" + difficulty + ", maxCost=" + maxCost
                + ", maxTime=" + maxTime + ", preferences=" + preferences + ", excludePlanned=" + excludePlanned + ", userId=" + userId + "]";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public Double getMaxCost() {
        return maxCost;
    }
    public void setMaxCost(Double maxCost) {
        this.maxCost = maxCost;
    }
    public Integer getMaxTime() {
        return maxTime;
    }
    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public Boolean getExcludePlanned() {
        return excludePlanned;
    }

    public void setExcludePlanned(Boolean excludePlanned) {
        this.excludePlanned = excludePlanned;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }
}
