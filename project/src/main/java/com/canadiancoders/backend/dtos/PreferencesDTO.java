package com.canadiancoders.backend.dtos;

public class PreferencesDTO 
{
    private UserPreferencesDTO preferences;
    private String token;
    public PreferencesDTO(UserPreferencesDTO preferences, String token) {
        this.preferences = preferences;
        this.token = token;
    }
    public UserPreferencesDTO getPreferences() {
        return preferences;
    }
    public void setPreferences(UserPreferencesDTO preferences) {
        this.preferences = preferences;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    @Override
    public String toString() {
        return "PreferencesDTO [preferences=" + preferences.toString() + ", token=" + token + "]";
    }

    
}
