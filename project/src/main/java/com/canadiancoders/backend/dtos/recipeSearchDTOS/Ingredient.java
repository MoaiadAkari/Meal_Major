package com.canadiancoders.backend.dtos.recipeSearchDTOS;

import java.math.BigDecimal;

public class Ingredient 
{
    private BigDecimal amount;
    private String unit;
    private String description;

    public Ingredient(BigDecimal amount, String unit, String description) {
        this.amount = amount;
        this.unit = unit;
        this.description = description;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    
}
