package com.canadiancoders.backend.recipes;

import com.canadiancoders.backend.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipe_pk;

    @Column(nullable = false)
    private String file_path;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int prepTime;

    @Column(nullable = false)
    private int cookTime;

    @Column(nullable = false)
    private double cost;   

    @Column(nullable = false)
    private String difficulty;

    @Column(length = 500)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    public Recipe() {}

    public Recipe(String file_path, String name, int prepTime, int cookTime, double cost, String difficulty, String description, User owner) {
        this.file_path = file_path;
        this.name = name;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.cost = cost;
        this.difficulty = difficulty;
        this.description = description;
        this.owner = owner;
    }

    public String getFile_path() { return file_path; }
    public Long getId() { return recipe_pk; }
    public String getName() { return name; }
    public int getPrepTime() { return prepTime; }
    public int getCookTime() { return cookTime; }
    public double getCost() { return cost; }
    public String getDifficulty() { return difficulty; }
    public String getDescription() { return description; }
    public User getOwner() { return owner; }

    public void setFile_path(String file_path) { this.file_path = file_path; }
    public void setName(String name) { this.name = name; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }
    public void setCost(double cost) { this.cost = cost; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setDescription(String description) { this.description = description; }
    public void setOwner(User owner) { this.owner = owner; }
}