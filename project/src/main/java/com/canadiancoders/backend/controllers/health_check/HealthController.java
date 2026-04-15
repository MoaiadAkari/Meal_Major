package com.canadiancoders.backend.controllers.health_check;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class HealthController 
{
    public HealthController(){}

    @GetMapping("health")
    public ResponseEntity<?> healthCheck()
    {
        return ResponseEntity.ok("OK");
    }
}
