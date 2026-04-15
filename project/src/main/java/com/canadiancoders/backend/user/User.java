package com.canadiancoders.backend.user;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_pk;  // cleaner naming

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String token;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column
    private String sex;

    // ===============================
    // DIETARY PREFERENCES
    // ===============================

    @ElementCollection
    @CollectionTable(
         name = "user_allergies",
         joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "allergy")
        private List<String> foodAllergies;

    @ElementCollection
    @CollectionTable(
        name = "user_intolerances",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "intolerance")
    private List<String> intolerances;

    @ElementCollection
    @CollectionTable(
        name = "user_diet_preferences",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "diet_preference")
    private List<String> preferences;

    // ===============================
    // CONSTRUCTORS
    // ===============================

    public User() {}

    public User(String name, String email, String password,
                Date dateOfBirth, String sex) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
    }

    // ===============================
    // GETTERS & SETTERS
    // ===============================

    public Long getUserPk() {
        return user_pk;
    }

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

    // ⚠ You should not return password in production
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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