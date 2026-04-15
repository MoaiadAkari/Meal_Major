package com.canadiancoders.backend.services;
import com.canadiancoders.backend.dtos.DessertResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CheatDayService {

    private final JdbcTemplate jdbc;

    public CheatDayService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public DessertResponseDTO getRandomDessert() {
        String sql = """
        SELECT 
        r.recipe_pk,
        r.name,
        i.file_path,
        rd.description
        FROM recipes r
        LEFT JOIN images i ON r.recipe_pk = i.recipe_fk
        LEFT JOIN recipe_descriptions rd ON r.recipe_pk = rd.recipe_fk
     WHERE r.owner_user_fk = 2
     ORDER BY RANDOM()
        LIMIT 1
""";

   try {
    return jdbc.queryForObject(sql, (rs, rowNum) ->
        new DessertResponseDTO(
            rs.getLong("recipe_pk"),
            rs.getString("name"),
            rs.getString("file_path"),
            rs.getString("description")
        )
    );
} catch (Exception e) {
    return null;
}
}
    
}
