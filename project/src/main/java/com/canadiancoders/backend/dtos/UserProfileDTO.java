package com.canadiancoders.backend.dtos;


import java.sql.Date;

public class UserProfileDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Date dateOfBirth;
    private String sex;

    public UserProfileDTO() {}

    // Getters & Setters
    public String getFirstName() { 
        return firstName; 
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public String getEmail() { 
        return email; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public Date getDateOfBirth() { 
        return dateOfBirth; 
    }

    public void setDateOfBirth1(Date dateOfBirth) { 
        this.dateOfBirth = dateOfBirth; 
    }

    public String getSex() { 
        return sex; 
    }

    public void setSex(String sex) { 
        this.sex = sex; 
    }

    public void setDateOfBirth(Date dateOfBirth2) {
        
        throw new UnsupportedOperationException("Unimplemented method 'setDateOfBirth'");
    }
}
