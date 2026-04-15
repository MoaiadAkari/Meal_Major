package com.canadiancoders.backend.dtos;

public class SelectGoalRequestDTO {

    private Integer goalId;
    private String startDate;
    private Integer weeklyPlanId;

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getWeeklyPlanId() {
        return weeklyPlanId;
    }

    public void setWeeklyPlanId(Integer weeklyPlanId) {
        this.weeklyPlanId = weeklyPlanId;
    }
}