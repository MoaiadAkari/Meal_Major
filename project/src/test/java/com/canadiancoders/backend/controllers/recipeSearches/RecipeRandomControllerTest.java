package com.canadiancoders.backend.controllers.recipeSearches;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import com.canadiancoders.backend.controllers.RecipeRandomController;
import com.canadiancoders.backend.dtos.RecipeListItemDTO;
import com.canadiancoders.backend.services.RecipeService;

@WebMvcTest(controllers = RecipeRandomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RecipeRandomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Test
    void getRandomRecipes() throws Exception {

        List<RecipeListItemDTO> mockRecipes = List.of(
            new RecipeListItemDTO(1L, "path/to/image.jpg", "Recipe 1", 10, 20, 5.0, "Easy", "Delicious recipe 1"),
            new RecipeListItemDTO(2L, "path/to/image2.jpg", "Recipe 2", 15, 25, 7.0, "Medium", "Delicious recipe 2"),
            new RecipeListItemDTO(3L, "path/to/image3.jpg", "Recipe 3", 20, 30, 10.0, "Hard", "Delicious recipe 3")
        );

        when( recipeService.getRandomRecipes()).thenReturn(mockRecipes);

        mockMvc.perform(get("/recipes/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Recipe 1"))
                .andExpect(jsonPath("$[1].name").value("Recipe 2"))
                .andExpect(jsonPath("$[2].name").value("Recipe 3"));
    }
}
