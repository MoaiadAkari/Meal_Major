package com.canadiancoders.backend.repository;

import com.canadiancoders.backend.dtos.CreateRecipeRequestDTO;
import com.canadiancoders.backend.dtos.CreateRecipeRequestDTO.IngredientDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RecipeWriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecipeWriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // My recipes
    // IMPORTANT: Your pasted schema does NOT have owner_user_fk or time.
    public List<Map<String, Object>> getMyRecipesForUser(int userPk, String q) {
        String sql = """
            SELECT r.recipe_pk,
                   r.name,
                   r.preptime,
                   r.cooktime,
                   r.cost,
                   d.difficulty,
                   r.servings
            FROM recipes r
            LEFT JOIN ref_difficulty d ON r.difficulty = d.difficulty_pk
            WHERE r.owner_user_fk = ?
        """;

        List<Object> params = new ArrayList<>();
        params.add(userPk);

        if (q != null && !q.isBlank()) {
            sql += " AND LOWER(r.name) LIKE ?";
            params.add("%" + q.trim().toLowerCase() + "%");
        }

        sql += " ORDER BY r.recipe_pk DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // Create recipe + description + ingredients + joins
    @Transactional
    public int createRecipe(CreateRecipeRequestDTO body, long userPk) {

        // recipes table columns (Postgres: preptime/cooktime are lowercase in reality)
        String insertRecipeSql = """
            INSERT INTO recipes (name, preptime, cooktime, cost, difficulty, servings, owner_user_fk)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING recipe_pk
        """;

        Integer recipePk = jdbcTemplate.queryForObject(
                insertRecipeSql,
                Integer.class,
                body.getName(),
                body.getPrepTime(),
                body.getCookTime(),
                body.getCost(),
                body.getDifficultyPk(),
                body.getServings(),
                userPk
        );

        if (recipePk == null) throw new RuntimeException("Failed to create recipe");

        // recipe_descriptions
        if (body.getDescription() != null && !body.getDescription().isBlank()) {
            jdbcTemplate.update(
                    "INSERT INTO recipe_descriptions (recipe_fk, description) VALUES (?, ?)",
                    recipePk,
                    body.getDescription()
            );
        }

        // Recipe_ingredients
        batchInsertIngredients(recipePk, body.getIngredients());

        // join tables (recipe_fk + *_fk)
        batchInsertJoin("join_recipes_allergens", "allergen_fk", recipePk, body.getAllergenPks());
        batchInsertJoin("join_recipes_diet_health", "health_fk", recipePk, body.getHealthPks());
        batchInsertJoin("join_recipes_diet_belief", "belief_fk", recipePk, body.getBeliefPks());

        return recipePk;
    }

    private void batchInsertIngredients(int recipePk, List<IngredientDTO> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return;

        String sql = """
            INSERT INTO recipe_ingredients (recipe_fk, amount, unit, ingredient_desc)
            VALUES (?, ?, ?, ?)
        """;

        jdbcTemplate.batchUpdate(sql, ingredients, 200, (ps, ing) -> {
            ps.setInt(1, recipePk);

            BigDecimal amount = ing.getAmount();
            if (amount == null) {
                ps.setNull(2, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(2, amount);
            }

            ps.setString(3, ing.getUnit());
            ps.setString(4, ing.getIngredientDesc());
        });
    }

    private void batchInsertJoin(String table, String fkColumn, int recipePk, List<Integer> fks) {
        if (fks == null || fks.isEmpty()) return;

        String sql = "INSERT INTO " + table + " (recipe_fk, " + fkColumn + ") VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, fks, 200, (ps, fk) -> {
            ps.setInt(1, recipePk);
            ps.setInt(2, fk);
        });
    }
}