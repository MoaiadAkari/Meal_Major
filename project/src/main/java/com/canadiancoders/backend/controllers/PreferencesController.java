package com.canadiancoders.backend.controllers;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.canadiancoders.backend.dtos.PreferencesDTO;
import com.canadiancoders.backend.dtos.loginDTOS.InformedToken;


@RestController
@RequestMapping("/userPreferences")
public class PreferencesController {
	
	private JdbcTemplate jdbc;
	
	public PreferencesController(JdbcTemplate jdbc)
	{
		this.jdbc=jdbc;
	}
	
	@PostMapping
	public InformedToken saveUserData(@RequestBody PreferencesDTO signup ) 
    {
        System.out.println("Preferences received data: \n"+signup);

        Long user_pk = jdbc.queryForObject("SELECT user_pk FROM users WHERE token = ?",Long.class, signup.getToken());

        List<String> dietPrefs =  signup.getPreferences().getDietPreferences();
        if (dietPrefs != null && !dietPrefs.isEmpty()) {
            jdbc.batchUpdate(
                "INSERT INTO user_diet_preferences (user_id, diet_preference) VALUES (?,?)",
                dietPrefs,
                dietPrefs.size(),
                (ps, diet) -> {
                    ps.setLong(1, user_pk);
                    ps.setString(2, diet);
                }
            );
        }
        List<String> allergies =  signup.getPreferences().getAllergies();
        
        jdbc.batchUpdate(
            "INSERT INTO user_allergies (user_id, allergy) VALUES (?,?)",
            allergies,
            allergies.size(),
            (ps, allergy) -> {
                ps.setLong(1, user_pk);
                ps.setString(2, allergy);
            }
        );

        List<String> intolerances =  signup.getPreferences().getIntolerances();
        
        jdbc.batchUpdate(
            "INSERT INTO user_intolerances (user_id, intolerance) VALUES (?,?)",
            intolerances,
            intolerances.size(),
            (ps, allergy) -> {
                ps.setLong(1, user_pk);
                ps.setString(2, allergy);
            }
        );
    
		return new InformedToken("Success", "", true, user_pk);
	}

}
