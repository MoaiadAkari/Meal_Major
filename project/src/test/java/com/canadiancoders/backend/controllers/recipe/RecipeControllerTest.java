package com.canadiancoders.backend.controllers.recipe;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import com.canadiancoders.backend.controllers.RecipeController;
import com.canadiancoders.backend.dtos.CreateRecipeRequestDTO;
import com.canadiancoders.backend.dtos.CreateRecipeRequestDTO.IngredientDTO;
import com.canadiancoders.backend.repository.RecipeWriteRepository;

@WebMvcTest(controllers = RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RecipeControllerTest 
{
    @MockBean
    private JdbcTemplate jdbc;

    @MockBean
    private RecipeWriteRepository recipeWriteRepo;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDifficulties() throws Exception {

        List<Map<String,Object>> mockDifficulty = List.of(Map.of("difficulty","Easy"),Map.of("difficulty","Medium"), Map.of("difficulty","Hard"));

        when(jdbc.queryForList(anyString())).thenReturn(mockDifficulty);

        mockMvc.perform(get("/api/recipes/difficulties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockDifficulty)));
    }

    @Test
    void getRecipeById() throws Exception {

        List<Map<String,Object>> mockRecipe = List.of(Map.of(
            "id", 1,
            "name", "Test Recipe",
            "preptime", 10,
            "cooktime", 20,
            "cost", 5.0,
            "servings", 4,
            "difficulty", "Easy",
            "description", "This is a test recipe."
        ));

        List<String> mockAllergens = List.of("Peanuts", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("Lactose-Free", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("Halal", "Kosher", "Vegetarian");

        List<String> mockIngredients = List.of("1 cup flour", "2 eggs", "1/2 cup sugar");

        when(jdbc.queryForList(anyString(), anyInt())).thenReturn(mockRecipe);
        when(jdbc.<String>queryForList(contains("file_path"), eq(String.class), any())).thenReturn(List.of("imagefilepath"));
        when(jdbc.<String>queryForList(contains("a.allergen"), eq(String.class), any())).thenReturn(mockAllergens);
        when(jdbc.<String>queryForList(contains("h.health"), eq(String.class), any())).thenReturn(mockHealth);
        when(jdbc.<String>queryForList(contains("b.belief"), eq(String.class), any())).thenReturn(mockBelief);
        when(jdbc.query(anyString(), ArgumentMatchers.<RowMapper<String>>any())).thenReturn(mockIngredients);
        
        mockMvc.perform(get("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Recipe"))
                .andExpect(jsonPath("$.preptime").value(10))
                .andExpect(jsonPath("$.cooktime").value(20))
                .andExpect(jsonPath("$.cost").value(5.0))
                .andExpect(jsonPath("$.servings").value(4))
                .andExpect(jsonPath("$.difficulty").value("Easy"))
                .andExpect(jsonPath("$.description").value("This is a test recipe."))
                .andExpect(jsonPath("$.file_path").value("imagefilepath"))
                .andExpect(jsonPath("$.dietaryRestrictions").isArray())
                .andExpect(jsonPath("$.intolerances").isArray())
                .andExpect(jsonPath("$.preferences").isArray())
                .andExpect(jsonPath("$.ingredients").isArray());
    }

    @Test 
    void getMyRecipes() throws Exception {

        when(recipeWriteRepo.getMyRecipesForUser(anyInt(),any())).thenReturn(List.of(
            Map.of(
                "id", 1,
                "name", "Test Recipe",
                "preptime", 10,
                "cooktime", 20,
                "cost", 5.0,
                "servings", 4,
                "difficulty", "Easy",
                "description", "This is a test recipe."
            )
        ));

        mockMvc.perform(get("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"))
                .andExpect(jsonPath("$[0].preptime").value(10))
                .andExpect(jsonPath("$[0].cooktime").value(20))
                .andExpect(jsonPath("$[0].cost").value(5.0))
                .andExpect(jsonPath("$[0].servings").value(4))
                .andExpect(jsonPath("$[0].difficulty").value("Easy"))
                .andExpect(jsonPath("$[0].description").value("This is a test recipe."));
    }

    @Test 
    void createRecipe() throws Exception {

         CreateRecipeRequestDTO recipe = new CreateRecipeRequestDTO();
         recipe.setName("Test Recipe");
         recipe.setPrepTime(10);
         recipe.setCookTime(20);
         recipe.setCost(5);
         recipe.setDifficultyPk(1); // Assuming 1 corresponds to "Easy"
         recipe.setServings(4);
         recipe.setDescription("This is a test recipe.");
         recipe.setIngredients(List.of(new IngredientDTO(), new IngredientDTO(), new IngredientDTO()));
         recipe.setAllergenPks(List.of(1, 2)); // Example allergen PKs
         recipe.setHealthPks(List.of(1)); // Example health PKs
         recipe.setBeliefPks(List.of(1)); // Example belief PKs

         when( recipeWriteRepo.createRecipe(any(), anyInt()) ).thenReturn(1);

        MockMultipartFile recipePart = new MockMultipartFile(
        "recipe",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(recipe)
);

         mockMvc.perform(
                multipart("/api/recipes")
                .file(recipePart)
                .sessionAttr("userId", 1))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateRecipeRequestDTO> captor = ArgumentCaptor.forClass(CreateRecipeRequestDTO.class);

        verify(recipeWriteRepo).createRecipe(
            captor.capture(),
            anyLong()
        );

        CreateRecipeRequestDTO args = captor.getValue();

        assertThat(args.getName()).isEqualTo("Test Recipe");
        assertThat(args.getPrepTime()).isEqualTo(10);
        assertThat(args.getCookTime()).isEqualTo(20);
        assertThat(args.getCost()).isEqualTo(5);
        assertThat(args.getDifficultyPk()).isEqualTo(1);
        assertThat(args.getServings()).isEqualTo(4);
        assertThat(args.getDescription()).isEqualTo("This is a test recipe.");
        assertThat(args.getIngredients()).isInstanceOf(List.class);
        assertThat(args.getAllergenPks()).isInstanceOf(List.class);
        assertThat(args.getHealthPks()).isInstanceOf(List.class);
        assertThat(args.getBeliefPks()).isInstanceOf(List.class);
    }

    @Test 
    void updateRecipe() throws Exception {

        List<String> mockAllergens = List.of("Peanuts", "Shellfish", "Dairy");
        List<String> mockHealth = List.of("Lactose-Free", "Low-Sodium", "Gluten-Free");
        List<String> mockBelief = List.of("Halal", "Kosher", "Vegetarian");

        List<String> mockIngredients = List.of("1 cup flour", "2 eggs", "1/2 cup sugar");

        Map<String,Object> mockRecipe = Map.ofEntries(
            Map.entry("id", 1),
            Map.entry("name", "Test Recipe"),
            Map.entry("preptime", 10),
            Map.entry("cooktime", 20),
            Map.entry("cost", 5.0),
            Map.entry("servings", 4),
            Map.entry("difficulty", "Easy"),
            Map.entry("description", "This is a test recipe."),
            Map.entry("dietaryRestrictions", mockAllergens),
            Map.entry("intolerances", mockHealth),
            Map.entry("preferences", mockBelief),
            Map.entry("ingredients", mockIngredients),
            Map.entry("image", "testImage")
        );

        

        when(jdbc.queryForObject(anyString(), eq(Integer.class), any())).thenReturn(1); 
        when(jdbc.update(anyString(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        when(jdbc.update(anyString(), any(), any())).thenReturn(1);
        when(jdbc.update(anyString(), ArgumentMatchers.<Object>any())).thenReturn(1);
        
        mockMvc.perform(put("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recipe updated successfully"));;
    }

}
