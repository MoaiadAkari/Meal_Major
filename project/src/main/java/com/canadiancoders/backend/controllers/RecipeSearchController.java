package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.recipeSearchDTOS.Recipe;
import com.canadiancoders.backend.dtos.recipeSearchDTOS.RecipeSearchRequestDTO;
import com.canadiancoders.backend.rowmappers.RecipeSearchRowMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin
public class RecipeSearchController {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public RecipeSearchController(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    private List<String> retrieveAllergensRef() {
        return jdbc.queryForList("SELECT allergen FROM ref_recipe_allergens ref ORDER BY ref.allergen_pk",
                String.class);
    }

    private List<String> retrieveBeliefsRef() {
        return jdbc.queryForList("SELECT belief FROM ref_recipe_diet_belief ref ORDER BY ref.belief_pk", String.class);
    }

    private List<String> retrieveHealthRef() {
        return jdbc.queryForList("SELECT health FROM ref_recipe_diet_health ref ORDER BY ref.health_pk", String.class);
    }

    private Integer retrieveDiffKey(String receivedDif)
    {
        if(receivedDif == null)
            return null;
        List<String> difficultyRef = jdbc.queryForList("SELECT difficulty FROM ref_difficulty ORDER BY difficulty_pk",
                String.class);
        Integer difficultyKey = 0;

        while (difficultyKey < difficultyRef.size()) {
            if (difficultyRef.get(difficultyKey).equals(receivedDif)) {
                break;
            }
            difficultyKey++;
        }

        // +1 to make it match difficulty_pk which starts at 1 not 0
        return ++difficultyKey;
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> receivePreferences() {
        List<String> preferences = new ArrayList<>();
        preferences.addAll(retrieveAllergensRef());
        preferences.addAll(retrieveBeliefsRef());
        preferences.addAll(retrieveHealthRef());

        return ResponseEntity.ok(preferences);
    }

    /**
     * Search recipes endpoint
     * Accepts RecipeSearchRequestDTO from frontend
     * returns a List of Recipe objects
     */

    @PostMapping("/search")
    public ResponseEntity<?> searchRecipes(@RequestBody RecipeSearchRequestDTO request) {

        System.out.println("Received request: "+ request.toString());

        // --------------------Converts preferences list into three lists based on what
        // is in database--------------//
        List<String> allergensRef = retrieveAllergensRef();
        List<String> diet_beliefsRef = retrieveBeliefsRef();
        List<String> diet_healthRef = retrieveHealthRef();

        Set<String> allergenSet = new HashSet<>(allergensRef);
        Set<String> beliefSet = new HashSet<>(diet_beliefsRef);
        Set<String> healthSet = new HashSet<>(diet_healthRef);

        List<String> allergens = new ArrayList<>();
        List<String> belief = new ArrayList<>();
        List<String> health = new ArrayList<>();

        if (request.getPreferences() == null || request.getPreferences().size() == 0) {
            allergens = null;
            belief = null;
            health = null;
        }
        else{
            for (String preference : request.getPreferences()) {
                if (allergenSet.contains(preference)) {
                    allergens.add(preference);
                } else if (beliefSet.contains(preference)) {
                    belief.add(preference);
                } else if (healthSet.contains(preference)) {
                    health.add(preference);
                } else {
                    return ResponseEntity.badRequest().body("One of the user preferences is not in the database");
                }
            }
            
            if(allergens.size() == 0)
                allergens = null;
            if(belief.size() == 0)
                belief = null;
            if(health.size() == 0)
                health = null;
        }

        System.out.println(allergens + "\n" + belief + "\n" + health + "\n");

        // ---------------------------------------------------------------------------------------------------------//

        Integer difficultyKey = retrieveDiffKey(request.getDifficulty());

        Boolean excludePlanned = request.getExcludePlanned() != null && request.getExcludePlanned();
        Long userId = request.getUserId();

        LocalDate weekStart = request.getWeekStart();

        Map<String, Object> params = new HashMap<>();
        params.put("name", request.getName());
        params.put("maxTime", request.getMaxTime());
        params.put("maxCost", request.getMaxCost());
        params.put("difficulty", difficultyKey);
        params.put("allergens", allergens == null ? null : new SqlParameterValue(Types.ARRAY, allergens.toArray(new String[0])));
        params.put("beliefs", belief == null ? null : new SqlParameterValue(Types.ARRAY, belief.toArray(new String[0])));
        params.put("health", health == null ? null : new SqlParameterValue(Types.ARRAY, health.toArray(new String[0])));
        params.put("excludePlanned", excludePlanned);
        params.put("userId", userId);
        params.put("weekStart", weekStart != null ? Date.valueOf(weekStart) : null );

        List<Recipe> recipes = namedJdbc.query(RECIPE_SEARCH, params, new RecipeSearchRowMapper());

        return ResponseEntity.ok(recipes);
    }

    // SQL queries for the searches

    private final static String CONDITIONAL_BELIEF_MATCHING = " SELECT 1"
            + " FROM unnest(:beliefs::text[]) AS required_beliefs(b)"
            + " WHERE NOT EXISTS ("
            + " SELECT 1"
            + " FROM join_recipes_diet_belief jb"
            + " JOIN ref_recipe_diet_belief bel ON jb.belief_fk = bel.belief_pk"
            + " WHERE jb.recipe_fk = recipe.recipe_pk AND bel.belief = required_beliefs.b)" ;
    
    private final static String CONDITIONAL_HEALTH_MATCHING = " SELECT 1"
            + " FROM unnest(:health::text[]) AS required_health(h)"
            + " WHERE NOT EXISTS ("
            + " SELECT 1"
            + " FROM join_recipes_diet_health jh"
            + " JOIN ref_recipe_diet_health hea ON hea.health_pk = jh.health_fk"
            + " WHERE jh.recipe_fk = recipe.recipe_pk AND hea.health = required_health.h)" ;

    private final static String CONDITIONAL_ALLERGENS =  " SELECT 1"
            + " FROM join_recipes_allergens j "
            + " JOIN ref_recipe_allergens al ON al.allergen_pk = j.allergen_fk "
            + " WHERE j.recipe_fk = recipe.recipe_pk AND al.allergen = ANY(:allergens::text[])";


    private final static String CONDITIONS = " WHERE (:name::text IS NULL OR LOWER(recipe.name) LIKE LOWER('%' || :name::text || '%'))"
            + " AND (:maxTime::integer IS NULL OR (recipe.prepTime + recipe.cookTime) <= :maxTime::integer)"
            + " AND (:difficulty::integer IS NULL OR :difficulty::integer = dif.difficulty_pk OR dif.difficulty_pk = :difficulty::integer-1)"
            + " AND (:maxCost::double precision IS NULL OR recipe.cost <= :maxCost::double precision)"
            + " AND (:allergens::text[] IS NULL "
            + " OR NOT EXISTS ("
            + CONDITIONAL_ALLERGENS
            + " ))"
            + " AND (:beliefs::text[] IS NULL "
            + " OR NOT EXISTS ("
            + CONDITIONAL_BELIEF_MATCHING
            + " ))"
            + " AND (:health::text[] IS NULL "
            + " OR NOT EXISTS ("
            + CONDITIONAL_HEALTH_MATCHING
            + " ))";

    private final static String EXCLUDE_PLANNED = " AND (:excludePlanned = FALSE OR NOT EXISTS ( "
            + "    SELECT 1 "
            + "    FROM weekly_plan wp "
            + "    LEFT JOIN daily_recipe dr_sun ON dr_sun.daily_pk = wp.sun "
            + "    LEFT JOIN daily_recipe dr_mon ON dr_mon.daily_pk = wp.mon "
            + "    LEFT JOIN daily_recipe dr_tue ON dr_tue.daily_pk = wp.tue "
            + "    LEFT JOIN daily_recipe dr_wed ON dr_wed.daily_pk = wp.wed "
            + "    LEFT JOIN daily_recipe dr_thu ON dr_thu.daily_pk = wp.thu "
            + "    LEFT JOIN daily_recipe dr_fri ON dr_fri.daily_pk = wp.fri "
            + "    LEFT JOIN daily_recipe dr_sat ON dr_sat.daily_pk = wp.sat "
            + "    JOIN LATERAL ( "
            + "        SELECT DISTINCT unnest(ARRAY[ "
            + "            dr_sun.breakfast_fk, dr_sun.morning_fk, dr_sun.lunch_fk, dr_sun.afternoon_fk, dr_sun.dinner_fk, dr_sun.night_fk, "
            + "            dr_mon.breakfast_fk, dr_mon.morning_fk, dr_mon.lunch_fk, dr_mon.afternoon_fk, dr_mon.dinner_fk, dr_mon.night_fk, "
            + "            dr_tue.breakfast_fk, dr_tue.morning_fk, dr_tue.lunch_fk, dr_tue.afternoon_fk, dr_tue.dinner_fk, dr_tue.night_fk, "
            + "            dr_wed.breakfast_fk, dr_wed.morning_fk, dr_wed.lunch_fk, dr_wed.afternoon_fk, dr_wed.dinner_fk, dr_wed.night_fk, "
            + "            dr_thu.breakfast_fk, dr_thu.morning_fk, dr_thu.lunch_fk, dr_thu.afternoon_fk, dr_thu.dinner_fk, dr_thu.night_fk, "
            + "            dr_fri.breakfast_fk, dr_fri.morning_fk, dr_fri.lunch_fk, dr_fri.afternoon_fk, dr_fri.dinner_fk, dr_fri.night_fk, "
            + "            dr_sat.breakfast_fk, dr_sat.morning_fk, dr_sat.lunch_fk, dr_sat.afternoon_fk, dr_sat.dinner_fk, dr_sat.night_fk "
            + "        ]) AS recipe_fk "
            + "    ) week_recipes ON TRUE "
            + "    WHERE wp.user_fk = :userId "
            + "      AND wp.start_date = :weekStart::date "
            + "      AND week_recipes.recipe_fk = recipe.recipe_pk "
            + ")) ";

    private final static String RECIPE_SEARCH = "SELECT recipe.recipe_pk, recipe.name, recipe.prepTime, recipe.cookTime, recipe.cost, recipe.servings, dif.difficulty, LEFT(rd.description, 350) AS description, im.file_path"
            + " FROM recipes AS recipe"
            + " INNER JOIN ref_difficulty dif ON recipe.difficulty = dif.difficulty_pk"
            + " INNER JOIN recipe_descriptions rd ON rd.recipe_fk = recipe.recipe_pk"
            + " LEFT JOIN images im ON im.recipe_fk = recipe.recipe_pk"
            + CONDITIONS
            + EXCLUDE_PLANNED
            + " AND (recipe.owner_user_fk IS NULL OR recipe.owner_user_fk != 2)"
            + ";";

}