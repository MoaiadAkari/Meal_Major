package com.canadiancoders.backend.dtos;

public class GoalDTO {
    private Integer id;
    private String name;
    private String description;

    // how many times the user needs to complete this goal before it's considered complete
    private Integer requiredIterations;

    public GoalDTO() {
    }

    public GoalDTO(Integer id, String name, String description, Integer requiredIterations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredIterations = requiredIterations;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRequiredIterations() {
        return requiredIterations;
    }

    public void setRequiredIterations(Integer requiredIterations) {
        this.requiredIterations = requiredIterations;
    }
}