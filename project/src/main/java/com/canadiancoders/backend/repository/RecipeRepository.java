package com.canadiancoders.backend.repository;

import com.canadiancoders.backend.recipes.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r WHERE r.owner.user_pk = :userId")
    List<Recipe> findByOwnerId(@Param("userId") Long userId);
}