package com.canadiancoders.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import com.canadiancoders.backend.dtos.CreateRecipeRequestDTO;
import com.canadiancoders.backend.repository.RecipeWriteRepository;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class RecipeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Added
    @Autowired
    private RecipeWriteRepository recipeWriteRepository;

    @GetMapping("/difficulties")
    public ResponseEntity<?> getDifficulties() {
        // CHECK DATABSE FOR: "difficulty" or "difficulty_name" and changed it.
        String sql = """            
            SELECT difficulty_pk, difficulty
            FROM ref_difficulty
            ORDER BY difficulty_pk
        """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable int recipeId) {
 System.out.println("Received request for recipe ID: " + recipeId);
        // - CHECK DATABASE FOR: If ref_difficulty column is "difficulty" (not difficulty_name), change d.difficulty_name to d.difficulty
        // - If recipes table does not have cost/serving_size/image_url yet, add columns OR remove from select below
        // Changed d.difficulty_name to d.difficulty to match database
        String recipeSql = """
            SELECT 
                r.recipe_pk AS id,
                r.name,
                r.preptime,
                r.cooktime,
                r.cost,
                r.servings,
                d.difficulty, 
                rd.description
            FROM recipes r
            LEFT JOIN ref_difficulty d
                ON r.difficulty = d.difficulty_pk
            LEFT JOIN recipe_descriptions rd
                ON r.recipe_pk = rd.recipe_fk
            WHERE r.recipe_pk = ?
        """;
        System.out.println("Fetching recipe with ID: " + recipeId);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(recipeSql, recipeId);
        System.out.println("Rows returned: " + rows.size());
       
        if (rows.isEmpty()) { // Return error if recipe not found
            System.out.println("No recipe found for ID: " + recipeId + " — returning 404");
            return ResponseEntity.status(404).body(Map.of("error", "Recipe not found"));
        }

        Map<String, Object> recipe = new HashMap<>(rows.get(0));
 System.out.println("Main recipe data: " + recipe);

        //Images
        System.out.println("Fetching images...");
        String imagesSql = """
            SELECT file_path
            FROM images
            WHERE recipe_fk = ?
            LIMIT 1
        """;

        List<String> images = jdbcTemplate.queryForList(imagesSql, String.class, recipeId);

        if (!images.isEmpty()) {
            recipe.put("file_path", images.get(0));
        } else {
            recipe.put("file_path", null);
        }
        System.out.println("Images: " + recipe.get("file_path"));

        // Allergens
        System.out.println("Fetching intolerances...");
        String intolerancesSql = """
            SELECT a.allergen
            FROM join_recipes_allergens j
            JOIN ref_recipe_allergens a
                ON j.allergen_fk = a.allergen_pk
            WHERE j.recipe_fk = ?
            ORDER BY a.allergen
        """;
        List<String> intolerances = jdbcTemplate.queryForList(intolerancesSql, String.class, recipeId);
        recipe.put("dietaryRestrictions", intolerances);
        System.out.println("Intolerances: " + recipe.get("intolerances"));
        // Dietary Restrictions (Diet Health)

        System.out.println("Fetching dietaryRestrictions...");
        String dietHealthSql = """
            SELECT h.health
            FROM join_recipes_diet_health j
            JOIN ref_recipe_diet_health h
                ON j.health_fk = h.health_pk
            WHERE j.recipe_fk = ?
            ORDER BY h.health
        """;
        List<String> dietaryRestrictions = jdbcTemplate.queryForList(dietHealthSql, String.class, recipeId);
        recipe.put("intolerances", dietaryRestrictions);
        System.out.println("Dietary restrictions: " + recipe.get("dietaryRestrictions"));

        // Preferences (Diet Belief)
        System.out.println("Fetching preferences...");
        String dietBeliefSql = """
            SELECT b.belief
            FROM join_recipes_diet_belief j
            JOIN ref_recipe_diet_belief b
                ON j.belief_fk = b.belief_pk
            WHERE j.recipe_fk = ?
            ORDER BY b.belief
        """;
       List<String> preferences = jdbcTemplate.queryForList(dietBeliefSql, String.class, recipeId);
        recipe.put("preferences", preferences); 
       //recipe.put("preferences", jdbcTemplate.queryForList(dietBeliefSql, recipeId));
        System.out.println("Preferences: " + recipe.get("preferences"));

        // Ingredients 
        try {
            System.out.println("Fetching ingredients...");
            String ingredientsSql = """
                SELECT amount, unit, ingredient_desc
                FROM recipe_ingredients
                WHERE recipe_fk = ?
                ORDER BY ingredient_pk
            """;
            List<String> ingredients = jdbcTemplate.query(ingredientsSql, (rs, rowNum) -> {
                BigDecimal amount = rs.getBigDecimal("amount");
                String unit = rs.getString("unit");
                String desc = rs.getString("ingredient_desc");

                StringBuilder sb = new StringBuilder();

                if (amount != null) {
                    sb.append(amount.stripTrailingZeros().toPlainString()).append(" ");
                }

                if (unit != null && !unit.equalsIgnoreCase("Unit")) {
                    sb.append(unit).append(" ");
                }

                sb.append(desc);

                return sb.toString().trim();
            },
            recipeId
            );
            
            recipe.put("ingredients", ingredients);

            System.out.println("Returning recipe data...");
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            System.err.println("Error fetching recipe: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch recipe"));
        }
    }

    // Search/list ONLY my recipes (requires recipes.owner_user_fk)
    @GetMapping
    public ResponseEntity<?> getMyRecipes(
            @RequestParam(required = false) String q,
            HttpSession session
    ) {

        Number userPkNum = (Number) session.getAttribute("userId");
        Integer userPk = userPkNum.intValue();

        return ResponseEntity.ok(recipeWriteRepository.getMyRecipesForUser(userPk, q));
    }

     // ----------------------------
    
    @PostMapping
    public ResponseEntity<?> createRecipe(
        @RequestPart("recipe") CreateRecipeRequestDTO body,
        @RequestPart(value = "image", required = false) MultipartFile file,
        HttpSession session
    ) {
        try {
            Number userPkNum = (Number) session.getAttribute("userId");
            Integer userPk = userPkNum.intValue();

            // Save recipe first
            int recipePk = recipeWriteRepository.createRecipe(body, userPk);

            // Handle file if uploaded
            if (file != null && !file.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();
                Path path = Paths.get("project/src/main/resources/static/sampleImages/uploads/" + fileName);
                Files.copy(file.getInputStream(), path);

                // store path in DB
                jdbcTemplate.update(
                    "INSERT INTO images (recipe_fk, file_path) VALUES (?, ?)",
                    recipePk,
                    "/sampleImages/uploads/" + fileName
                );
            }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("recipe_pk", recipePk));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
    @PutMapping("/{recipeId}")
    public ResponseEntity<?> updateRecipe(
            @PathVariable int recipeId,
            @RequestBody Map<String, Object> payload
    ) {

        try {

            // ==============================
            // BASIC FIELDS
            // ==============================

            String name = (String) payload.get("name");
            Integer prepTime = ((Number) payload.get("preptime")).intValue();
            Integer cookTime = ((Number) payload.get("cooktime")).intValue();
            Integer servings = ((Number) payload.get("servings")).intValue();
            Integer cost = ((Number) payload.get("cost")).intValue();
            String description = (String) payload.get("description");
            String difficultyStr = (String) payload.get("difficulty");

            Integer difficultyId = jdbcTemplate.queryForObject(
                    "SELECT difficulty_pk FROM ref_difficulty WHERE difficulty = ?",
                    Integer.class,
                    difficultyStr
            );

            jdbcTemplate.update("""
                UPDATE recipes
                SET name=?, preptime=?, cooktime=?, cost=?, servings=?, difficulty=?
                WHERE recipe_pk=?
            """,
                    name,
                    prepTime,
                    cookTime,
                    cost,
                    servings,
                    difficultyId,
                    recipeId
            );

            jdbcTemplate.update("""
                UPDATE recipe_descriptions
                SET description=?
                WHERE recipe_fk=?
            """,
                    description,
                    recipeId
            );

            // ==============================
            // INGREDIENTS
            // ==============================

            jdbcTemplate.update("DELETE FROM recipe_ingredients WHERE recipe_fk=?", recipeId);

            List<String> ingredients = (List<String>) payload.get("ingredients");

            for (String ingredient : ingredients) {
                jdbcTemplate.update("""
                    INSERT INTO recipe_ingredients (recipe_fk, ingredient_desc)
                    VALUES (?, ?)
                """, recipeId, ingredient);
            }

            // ==============================
            // ALLERGENS
            // ==============================

            jdbcTemplate.update("DELETE FROM join_recipes_allergens WHERE recipe_fk=?", recipeId);

            List<String> allergens = (List<String>) payload.get("dietaryRestrictions");

            for (String allergen : allergens) {
                Integer id = jdbcTemplate.queryForObject(
                        "SELECT allergen_pk FROM ref_recipe_allergens WHERE allergen = ?",
                        Integer.class,
                        allergen
                );

                jdbcTemplate.update("""
                    INSERT INTO join_recipes_allergens (recipe_fk, allergen_fk)
                    VALUES (?, ?)
                """, recipeId, id);
            }

            // ==============================
            // HEALTH
            // ==============================

            jdbcTemplate.update("DELETE FROM join_recipes_diet_health WHERE recipe_fk=?", recipeId);

            List<String> healthList = (List<String>) payload.get("intolerances");

            for (String health : healthList) {
                Integer id = jdbcTemplate.queryForObject(
                        "SELECT health_pk FROM ref_recipe_diet_health WHERE health = ?",
                        Integer.class,
                        health
                );

                jdbcTemplate.update("""
                    INSERT INTO join_recipes_diet_health (recipe_fk, health_fk)
                    VALUES (?, ?)
                """, recipeId, id);
            }

            // ==============================
            // BELIEFS
            // ==============================

            jdbcTemplate.update("DELETE FROM join_recipes_diet_belief WHERE recipe_fk=?", recipeId);

            List<String> beliefs = (List<String>) payload.get("preferences");

            for (String belief : beliefs) {
                Integer id = jdbcTemplate.queryForObject(
                        "SELECT belief_pk FROM ref_recipe_diet_belief WHERE belief = ?",
                        Integer.class,
                        belief
                );

                jdbcTemplate.update("""
                    INSERT INTO join_recipes_diet_belief (recipe_fk, belief_fk)
                    VALUES (?, ?)
                """, recipeId, id);
            }

            // ==============================
            // IMAGE
            // ==============================

            jdbcTemplate.update("DELETE FROM images WHERE recipe_fk=?", recipeId);

            String image = (String) payload.get("image");

            if (image != null && !image.isEmpty()) {
                jdbcTemplate.update("""
                    INSERT INTO images (recipe_fk, file_path)
                    VALUES (?, ?)
                """, recipeId, image);
            }

            return ResponseEntity.ok(Map.of("message", "Recipe updated successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}