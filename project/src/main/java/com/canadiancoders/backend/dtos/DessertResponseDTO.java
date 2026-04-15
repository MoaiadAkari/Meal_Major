package com.canadiancoders.backend.dtos;

public class DessertResponseDTO {
    private Long  id;
    private String name;
    private String imagePath;
    private String description;

    public DessertResponseDTO(){    
    }

    public DessertResponseDTO(Long id, String name, String imagePath, String description) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
