package com.canadiancoders.backend.dtos.loginDTOS;

public class InformedToken 
{
    private String message;
    private String token;
    private boolean success;
    private Long userId;
    
    public InformedToken(String message, String token, boolean success, Long userId) {
        this.message = message;
        this.token = token;
        this.success = success;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}
