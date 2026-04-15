package com.canadiancoders.backend.dtos;

import java.util.List;

public class UserDTO {

    private String name;
    private String email;
    private List<String> foodAllergies;
    private List<String> intolerances;
    private List<String> preferences;

    public UserDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFoodAllergies() {
        return foodAllergies;
    }

    public void setFoodAllergies(List<String> foodAllergies) {
        this.foodAllergies = foodAllergies;
    }

    public List<String> getIntolerances() {
        return intolerances;
    }

    public void setIntolerances(List<String> intolerances) {
        this.intolerances = intolerances;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }
}