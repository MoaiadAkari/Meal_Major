/*
package com.canadiancoders.backend.dtos;

import java.util.List;
import java.math.BigDecimal;

public class CreateRecipeRequestDTO {

    private String name;
    // Replaced "Time"
    private Integer prepTime;        // recipes.preptime (minutes)
    private Integer cookTime;        // recipes.cooktime (minutes)

    private Integer difficultyPk;  // FK -> ref_difficulty.difficulty_pk
    private String description;    // stored in recipe_descriptions
    private Integer cost;            // recipes.cost

    private List<Integer> allergenPks; // FK -> ref_recipe_allergens.allergens_pk
    private List<Integer> healthPks;   // FK -> ref_recipe_diet_health.health_pk
    private List<Integer> beliefPks;   // FK -> ref_recipe_diet_belief.belief_pk

    public CreateRecipeRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTime() { return time; }
    public void setTime(Integer time) { this.time = time; }

    public Integer getDifficultyPk() { return difficultyPk; }
    public void setDifficultyPk(Integer difficultyPk) { this.difficultyPk = difficultyPk; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Integer> getAllergenPks() { return allergenPks; }
    public void setAllergenPks(List<Integer> allergenPks) { this.allergenPks = allergenPks; }

    public List<Integer> getHealthPks() { return healthPks; }
    public void setHealthPks(List<Integer> healthPks) { this.healthPks = healthPks; }

    public List<Integer> getBeliefPks() { return beliefPks; }
    public void setBeliefPks(List<Integer> beliefPks) { this.beliefPks = beliefPks; }
}*/
package com.canadiancoders.backend.dtos;

import java.math.BigDecimal;
import java.util.List;

public class CreateRecipeRequestDTO {

    // recipes table
    private String name;

    // Schema has separate columns; replace your single "time"
    private Integer prepTime;        // recipes.preptime (minutes)
    private Integer cookTime;        // recipes.cooktime (minutes)

    private Integer cost;            // recipes.cost
    private Integer difficultyPk;    // recipes.difficulty (FK -> ref_difficulty.difficulty_pk)
    private Integer servings;        // recipes.servings

    // recipe_descriptions table
    private String description;      // recipe_descriptions.description

    // recipe_ingredients table
    private List<IngredientDTO> ingredients;

    // Join tables (optional, but your DTO already expects these)
    // join_recipes_allergens (allergen_fk -> ref_recipe_allergens.allergen_pk)
    private List<Integer> allergenPks;

    // join_recipes_diet_health (health_fk -> ref_recipe_diet_health.health_pk)
    private List<Integer> healthPks;

    // join_recipes_diet_belief (belief_fk -> ref_recipe_diet_belief.belief_pk)
    private List<Integer> beliefPks;

    public CreateRecipeRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrepTime() { return prepTime; }
    public void setPrepTime(Integer prepTime) { this.prepTime = prepTime; }

    public Integer getCookTime() { return cookTime; }
    public void setCookTime(Integer cookTime) { this.cookTime = cookTime; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public Integer getDifficultyPk() { return difficultyPk; }
    public void setDifficultyPk(Integer difficultyPk) { this.difficultyPk = difficultyPk; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<IngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientDTO> ingredients) { this.ingredients = ingredients; }

    public List<Integer> getAllergenPks() { return allergenPks; }
    public void setAllergenPks(List<Integer> allergenPks) { this.allergenPks = allergenPks; }

    public List<Integer> getHealthPks() { return healthPks; }
    public void setHealthPks(List<Integer> healthPks) { this.healthPks = healthPks; }

    public List<Integer> getBeliefPks() { return beliefPks; }
    public void setBeliefPks(List<Integer> beliefPks) { this.beliefPks = beliefPks; }

    // Nested DTO for recipe_ingredients
    public static class IngredientDTO {
        private BigDecimal amount;     // recipe_ingredients.amount DECIMAL(10,3)
        private String unit;           // recipe_ingredients.unit
        private String ingredientDesc; // recipe_ingredients.ingredient_desc

        public IngredientDTO() {}

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public String getIngredientDesc() { return ingredientDesc; }
        public void setIngredientDesc(String ingredientDesc) { this.ingredientDesc = ingredientDesc; }
    }
}