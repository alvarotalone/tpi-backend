package com.backend.tpi_backend.serviciousuarios.controller;

import com.backend.tpi_backend.serviciousuarios.dto.LoginRequest;
import com.backend.tpi_backend.serviciousuarios.dto.TokenResponse;
import com.backend.tpi_backend.serviciousuarios.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Autenticación", description = "Login de usuarios mediante integración con Keycloak")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse token = authService.login(request);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            // credenciales inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }
}
