package com.canadiancoders.backend.dtos.recipeSearchDTOS;


public class Recipe {

    private Long id; 
    private String name;
    private String description;
    private String difficulty;
    private Double cost;
    private Integer prepTime; 
    private Integer cookTime; 
    private String file_path; 

    public Recipe() {
    }

    public Recipe(Long id, String name, String description, String difficulty, Double cost, Integer prepTime,
            Integer cookTime, String file_path) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.cost = cost;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.file_path = file_path;
    }



    public Integer getPrepTime() {
        return prepTime;
    }



    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }



    public Integer getCookTime() {
        return cookTime;
    }



    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }


    public String getFile_path() {
        return file_path;
    }


    public void setFile_path(String img) {
        this.file_path = img;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getDifficulty() {
        return difficulty;
    }


    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }


    public Double getCost() {
        return cost;
    }


    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
