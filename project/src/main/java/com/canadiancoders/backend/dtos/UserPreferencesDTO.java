package com.canadiancoders.backend.dtos;


import java.util.List;

public class UserPreferencesDTO {

    private List<String> dietPreferences;
    private List<String> allergies;
    private List<String> intolerances;

    public UserPreferencesDTO() {}

    public UserPreferencesDTO(List<String> dietPreferences, List<String> allergies, List<String> intolerances) {
        this.dietPreferences = dietPreferences;
        this.allergies = allergies;
        this.intolerances = intolerances;
    }

    // Getters & Setters
    public List<String> getDietPreferences() { 
        return dietPreferences; 
    }

    public void setDietPreferences(List<String> dietPreferences) {
        this.dietPreferences = dietPreferences;
    }

    public List<String> getIntolerances() { 
        return intolerances; 
    }

    public void setIntolerances(List<String> intolerances) {
        this.intolerances = intolerances;
    }

    public List<String> getAllergies() { 
         return allergies; 
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    @Override
    public String toString() {
        return "UserPreferencesDTO [dietPreferences=" + (dietPreferences == null? null : dietPreferences.toString()) + ", allergies=" + (allergies == null ? null : allergies.toString())+  ", intolerances=" +  (intolerances == null ? null : intolerances.toString())+"]";
    }

}
