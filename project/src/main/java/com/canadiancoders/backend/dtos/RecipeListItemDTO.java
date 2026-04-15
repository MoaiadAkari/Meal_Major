package com.canadiancoders.backend.dtos;

public class RecipeListItemDTO {
    private Long id;
    private String file_path;
    private String name;
    private int prepTime;
    private int cookTime;
    private double cost;
    private String difficulty;
    private String description;

    public RecipeListItemDTO(Long id, String file_path, String name, int prepTime, int cookTime, double cost, String difficulty, String description) {
        this.id = id;
        this.file_path = file_path;
        this.name = name;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.cost = cost;
        this.difficulty = difficulty;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getFile_path() { return file_path; }
    public String getName() { return name; }
    public int getPrepTime() { return prepTime; }
    public int getCookTime() { return cookTime; }
    public double getCost() { return cost;}
    public String getDifficulty() { return difficulty; }
    public String getDescription() { return description; }
}