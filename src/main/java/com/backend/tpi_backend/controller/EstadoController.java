package com.backend.tpi_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // Marca esta clase como un controlador REST (devuelve JSON o texto)
@RequestMapping("/api")  // Prefijo común para los endpoints
public class EstadoController {

    // Inyecta el valor del puerto configurado en application.properties
    @Value("${server.port}")
    private String puerto;

    // Endpoint GET: http://localhost:8080/api/estado
    @GetMapping("/estado")
    public String estado() {
        return "✅ Servidor funcionando correctamente en el puerto " + puerto;
    }
}

