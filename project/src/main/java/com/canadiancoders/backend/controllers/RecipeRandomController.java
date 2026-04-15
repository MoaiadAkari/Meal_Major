package com.canadiancoders.backend.controllers;

import com.canadiancoders.backend.dtos.RecipeListItemDTO;
import com.canadiancoders.backend.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@CrossOrigin
public class RecipeRandomController {

    private final RecipeService recipeService;

    public RecipeRandomController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<RecipeListItemDTO>> getRandomRecipes() {
        return ResponseEntity.ok(recipeService.getRandomRecipes());
    }
}