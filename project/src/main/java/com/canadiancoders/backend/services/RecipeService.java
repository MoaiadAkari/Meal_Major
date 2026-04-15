package com.canadiancoders.backend.services;

import com.canadiancoders.backend.dtos.RecipeListItemDTO;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Service
public class RecipeService {

    private final JdbcTemplate jdbcTemplate;

    public RecipeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecipeListItemDTO> getRandomRecipes() {

        String sql = """
            SELECT 
                r.recipe_pk,
                i.file_path,
                r.name,
                r.prepTime,
                r.cookTime,
                r.cost,
                d.difficulty,
                LEFT(rd.description, 350) AS description
            FROM recipes r
            LEFT JOIN images i 
                ON r.recipe_pk = i.recipe_fk
            LEFT JOIN ref_difficulty d 
                ON r.difficulty = d.difficulty_pk
            LEFT JOIN recipe_descriptions rd 
                ON r.recipe_pk = rd.recipe_fk
            WHERE (owner_user_fk IS NULL OR owner_user_fk != 2) -- Exclude dessert recipes
            ORDER BY RANDOM()
            LIMIT 3
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            new RecipeListItemDTO(
                rs.getLong("recipe_pk"),
                rs.getString("file_path"),
                rs.getString("name"),
                rs.getInt("prepTime"),
                rs.getInt("cookTime"),
                rs.getDouble("cost"),
                rs.getString("difficulty"),
                rs.getString("description")
            )
        );
    }
}