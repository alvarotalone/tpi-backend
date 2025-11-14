package com.backend.tpi_backend.serviciocamiones.controller;

//import com.backend.tpi_backend.serviciocamiones.model.DetalleDisponibilidad;
import com.backend.tpi_backend.serviciocamiones.service.DetalleDisponibilidadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/camiones/{dominio}/disponibilidades")
@Tag(name = "Disponibilidad de Camiones", description = "Gestión de disponibilidad de camiones por fecha")
public class DetalleDisponibilidadController {

    private final DetalleDisponibilidadService detalleService;

    public DetalleDisponibilidadController(DetalleDisponibilidadService detalleService) {
        this.detalleService = detalleService;
    }

    @PostMapping("/verificar")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable String dominio,
            @RequestBody Map<String, String> body) {

        String fechaInicio = body.get("fechaInicio");
        String fechaFin = body.get("fechaFin");

        if (fechaInicio == null || fechaFin == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean disponible = detalleService.estaDisponible(dominio, fechaInicio, fechaFin);
        return ResponseEntity.ok(disponible);
    }
}
