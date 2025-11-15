package com.backend.tpi_backend.serviciointegracionkeycloak.controller;

import com.backend.tpi_backend.serviciointegracionkeycloak.dto.LoginRequest;
import com.backend.tpi_backend.serviciointegracionkeycloak.dto.TokenResponse;
import com.backend.tpi_backend.serviciointegracionkeycloak.service.KeycloakAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak")
public class KeycloakAuthController {

    private final KeycloakAuthService authService;

    public KeycloakAuthController(KeycloakAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
