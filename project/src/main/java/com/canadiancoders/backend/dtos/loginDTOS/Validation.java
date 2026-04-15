package com.canadiancoders.backend.dtos.loginDTOS;

public class Validation 
{
    boolean valid;

    public Validation(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
