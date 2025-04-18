package com.salihapp.keycloak.controller;

import com.salihapp.keycloak.config.ApiPaths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.Keycloak.BASE)
public class KeycloakController {

    // Admin Endpoint
    @GetMapping(ApiPaths.Keycloak.ADMIN)
    public ResponseEntity<String> adminDetails() {
        return ResponseEntity.ok("Hello Admin");
    }

    // User Endpoint
    @GetMapping(ApiPaths.Keycloak.USER)
    public ResponseEntity<String> userDetails() {
        return ResponseEntity.ok("Hello User");
    }

    // Home Endpoint
    @GetMapping(ApiPaths.Keycloak.HOME)
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello Everyone!");
    }
}