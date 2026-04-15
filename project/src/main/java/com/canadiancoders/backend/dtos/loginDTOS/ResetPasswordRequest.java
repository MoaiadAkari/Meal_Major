package com.canadiancoders.backend.dtos.loginDTOS;

public class ResetPasswordRequest {
    public String token;
    public String newPassword;
    public String confirm_password;
   
    public ResetPasswordRequest(String token, String newPassword, String confirm_password)
    {
        this.token = token;
        this.newPassword = newPassword;
        this.confirm_password = confirm_password;
    }
   
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    
}
