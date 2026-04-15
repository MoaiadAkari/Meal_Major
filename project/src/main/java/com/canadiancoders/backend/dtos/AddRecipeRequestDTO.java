package com.canadiancoders.backend.dtos;

public class AddRecipeRequestDTO {

    private String day;
    private String meal;
    private Integer recipeId;
    private String weekStart; // ISO date from frontend

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getMeal() { return meal; }
    public void setMeal(String meal) { this.meal = meal; }

    public Integer getRecipeId() { return recipeId; }
    public void setRecipeId(Integer recipeId) { this.recipeId = recipeId; }

    public String getWeekStart() { return weekStart; }
    public void setWeekStart(String weekStart) { this.weekStart = weekStart; }
}
