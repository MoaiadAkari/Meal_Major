package com.canadiancoders.backend.controllers.recipeSearches;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import com.canadiancoders.backend.controllers.RecipeSearchController;
import com.canadiancoders.backend.dtos.recipeSearchDTOS.Recipe;
import com.canadiancoders.backend.dtos.recipeSearchDTOS.RecipeSearchRequestDTO;

@WebMvcTest(controllers = RecipeSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RecipeSearchControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @MockBean
    private NamedParameterJdbcTemplate jdbcNamed;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void receivePreferences() throws Exception {
        
        List<String> mockAllergens = List.of("Peanuts", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("Lactose-Free", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("Halal", "Kosher", "Vegetarian");

        List<String> mockPreferences = new ArrayList<>(9);
        mockPreferences.addAll(mockAllergens);
        mockPreferences.addAll(mockHealth);
        mockPreferences.addAll(mockBelief);

        String preferencesJson = objectMapper.writeValueAsString(mockPreferences);

        when( jdbc.queryForList(startsWith("SELECT allergen"), eq(String.class))).thenReturn(mockAllergens);
        when( jdbc.queryForList(startsWith("SELECT health"), eq(String.class))).thenReturn(mockHealth);
        when( jdbc.queryForList(startsWith("SELECT belief"), eq(String.class))).thenReturn(mockBelief);

        mockMvc.perform(get("/api/recipes/preferences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(preferencesJson));
    }

    @Test
    void searchRecipes() throws Exception {
        
        RecipeSearchRequestDTO request = new RecipeSearchRequestDTO(null, "Easy", null, null, null, null, null, null);

        List<String> mockAllergens = List.of("Peanuts", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("Lactose-Free", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("Halal", "Kosher", "Vegetarian");
        List<String> mockDifficulty = List.of("Easy", "Medium", "Hard");

        List<Recipe> mockRecipes = List.of(
            new Recipe(1L, "Recipe 1", "Description 1", "Easy", 10.0, 30, 30, "file_path_1"),
            new Recipe(2L, "Recipe 2", "Description 2", "Easy", 20.0, 60, 60, "file_path_2"),
            new Recipe(3L, "Recipe 3", "Description 3", "Easy", 30.0, 90, 90, "file_path_3")
        );

        when( jdbc.queryForList(startsWith("SELECT allergen"), eq(String.class))).thenReturn(mockAllergens);
        when( jdbc.queryForList(startsWith("SELECT health"), eq(String.class))).thenReturn(mockHealth);
        when( jdbc.queryForList(startsWith("SELECT belief"), eq(String.class))).thenReturn(mockBelief);
        when( jdbc.queryForList(startsWith("SELECT difficulty"), eq(String.class))).thenReturn(mockDifficulty);

        when( jdbcNamed.<Recipe>query(anyString(), ArgumentMatchers.<Map<String, Object>>any(),
                ArgumentMatchers.<RowMapper<Recipe>>any())).thenReturn(mockRecipes);

        String recipesJson = objectMapper.writeValueAsString(mockRecipes);

        mockMvc.perform(post("/api/recipes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(recipesJson));


        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        verify(jdbcNamed).<Recipe>query(
            anyString(), 
            captor.capture(),
            ArgumentMatchers.<RowMapper<Recipe>>any());

        Map<String, Object> args = captor.getValue();

        assertThat(args.get("difficulty")).isEqualTo(1); // Tests "Easy" mapped to 1 according to the test reference
    }
}
