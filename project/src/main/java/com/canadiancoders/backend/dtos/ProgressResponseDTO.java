package com.canadiancoders.backend.dtos;


public class ProgressResponseDTO {
    
 private int total;
 private double percentage;
 private int completed;


 // constructors (default and parametrized )
  public ProgressResponseDTO() {}

    public ProgressResponseDTO(int total, int completed) {
        this.total = total;
        this.completed = completed;
        updatePercentage();
    }


    // calculate percentage / update it whenever anything is changed (the method is called in setters)
    private void updatePercentage(){
        if (total == 0) {
            this.percentage = 0;
        } else {
            this.percentage = (completed * 100.0) / total; 
        }
    }

    // Getters and Setters
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        updatePercentage();
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
        updatePercentage();
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
 
 }