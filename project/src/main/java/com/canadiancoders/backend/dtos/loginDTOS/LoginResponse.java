package com.canadiancoders.backend.dtos.loginDTOS;

public class LoginResponse 
{
    boolean success;
    String errorMessage;
    private Long userId;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LoginResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    public LoginResponse(boolean success) {
        this.success = success;
    }
    public LoginResponse(boolean success, Long userId) {
        this.success = success;
        this.userId = userId;
    }

    
}
