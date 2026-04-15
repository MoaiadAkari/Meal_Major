package com.canadiancoders.backend.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.canadiancoders.backend.dtos.recipeSearchDTOS.Recipe;

public class RecipeSearchRowMapper implements RowMapper<Recipe>
{
    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(rs.getLong("recipe_pk"));
        recipe.setName(rs.getString("name"));
        recipe.setDescription(rs.getString("description"));
        recipe.setDifficulty(rs.getString("difficulty"));
        recipe.setPrepTime(rs.getInt("prepTime"));
        recipe.setCookTime(rs.getInt("cookTime"));
        recipe.setCost(rs.getDouble("cost"));
        recipe.setFile_path(rs.getString("file_path"));

        return recipe;
    }

    // private List<String> extractStringArray(ResultSet rs, String column) throws SQLException 
    // { 
    //     Array array = rs.getArray(column); 
    //     if (array == null) 
    //     { 
    //         return new ArrayList<>(); 
    //     } 
    //     String[] values = (String[]) array.getArray(); 
    //     return new ArrayList<>(Arrays.asList(values)); 
    // }

    // private List<Ingredient> extractIngredientArray(ResultSet rs) throws SQLException 
    // {
    //     Array amounts = rs.getArray("ing_amounts");
    //     Array units = rs.getArray("ing_units");
    //     Array descriptions = rs.getArray("ing_descriptions");
        
    //     if(amounts == null || units == null || descriptions == null )
    //     {
    //         return new ArrayList<>();
    //     }
    //     List<Ingredient> ingredients = new ArrayList<Ingredient>();
    //     Object[] amountsArr = (Object[]) amounts.getArray();
    //     String[] unitsArr = (String[]) units.getArray();
    //     String[] descriptionsArr = (String[]) descriptions.getArray();

    //     if(amountsArr.length != unitsArr.length || amountsArr.length != descriptionsArr.length)
    //     {
    //         System.out.println("ALERT: Mismatched ingredient array sizes returned from databse. No ingredients handled");
    //         return new ArrayList<>();
    //     }

    //     for(int i = 0; i < amountsArr.length; i++)
    //     {
    //         ingredients.add(new Ingredient((BigDecimal) amountsArr[i], unitsArr[i], descriptionsArr[i]));
    //     }

    //     return ingredients;
    // }
}
