package com.canadiancoders.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/recipes")
public class DeleteController {

    private JdbcTemplate jdbc;

    public DeleteController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Integer id) {

        String deletedName = jdbc.queryForObject(
                "SELECT name FROM recipes WHERE recipe_pk = ?",
                String.class,
                id
        );

        int deletedRows = jdbc.update(
                "DELETE FROM recipes WHERE recipe_pk = ?",
                id
        );

        if (deletedRows > 0) {
            return ResponseEntity.ok("Deleted: " + deletedName);
        } else {
            return ResponseEntity.internalServerError().body("Delete failed");
        }
    }
}
