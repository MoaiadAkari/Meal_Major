package com.canadiancoders.backend.dtos;

// this DTO is used to send back all info related to a selected goal
// (goal info + progress + completion status)
public class PlanGoalResponseDTO {

    // primary key of the join table (PlanGoal)
    private Integer joinPk;

    // which weekly plan this belongs to
    private Integer weeklyPk;

    // goal info
    private Integer goalPk;
    private String goal;

    // progress tracking (how many times user did the goal)
    private Integer progressCount;

    // whether the goal is completed or not
    private Boolean complete;

    // NEW: how many iterations are required to complete the goal
    private Integer requiredIterations;

    // message (used for responses like "progress updated")
    private String message;

    public PlanGoalResponseDTO() {
    }

    // basic constructor (no message)
    public PlanGoalResponseDTO(Integer joinPk,
                               Integer weeklyPk,
                               Integer goalPk,
                               String goal,
                               Integer progressCount,
                               Boolean complete,
                               Integer requiredIterations) {
        this.joinPk = joinPk;
        this.weeklyPk = weeklyPk;
        this.goalPk = goalPk;
        this.goal = goal;
        this.progressCount = progressCount;
        this.complete = complete;
        this.requiredIterations = requiredIterations;
    }

    // constructor with message (used after updates, etc.)
    public PlanGoalResponseDTO(Integer joinPk,
                               Integer weeklyPk,
                               Integer goalPk,
                               String goal,
                               Integer progressCount,
                               Boolean complete,
                               Integer requiredIterations,
                               String message) {
        this.joinPk = joinPk;
        this.weeklyPk = weeklyPk;
        this.goalPk = goalPk;
        this.goal = goal;
        this.progressCount = progressCount;
        this.complete = complete;
        this.requiredIterations = requiredIterations;
        this.message = message;
    }

    public Integer getJoinPk() {
        return joinPk;
    }

    public void setJoinPk(Integer joinPk) {
        this.joinPk = joinPk;
    }

    public Integer getWeeklyPk() {
        return weeklyPk;
    }

    public void setWeeklyPk(Integer weeklyPk) {
        this.weeklyPk = weeklyPk;
    }

    public Integer getGoalPk() {
        return goalPk;
    }

    public void setGoalPk(Integer goalPk) {
        this.goalPk = goalPk;
    }

    public String getGoalName() {
        return goal;
    }

    public void setGoalName(String goal) {
        this.goal = goal;
    }

    public Integer getProgressCount() {
        return progressCount;
    }

    public void setProgressCount(Integer progressCount) {
        this.progressCount = progressCount;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    // getter/setter for required iterations
    public Integer getRequiredIterations() {
        return requiredIterations;
    }

    public void setRequiredIterations(Integer requiredIterations) {
        this.requiredIterations = requiredIterations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}