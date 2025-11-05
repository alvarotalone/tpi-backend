package com.backend.tpi_backend.serviciocamiones.controller;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.service.CamionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/camiones")
@Tag(name = "Camiones", description = "Gestión de camiones y disponibilidad")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping
    public List<Camion> listarCamiones() {
        return camionService.obtenerTodos();
    }

    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> obtenerCamion(@PathVariable String dominio) {
        return camionService.obtenerPorDominio(dominio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camion> crearCamion(@RequestBody Camion camion) {
        Camion nuevo = camionService.guardar(camion);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{dominio}")
    public ResponseEntity<Camion> actualizarCamion(@PathVariable String dominio, @RequestBody Camion camion) {
        camion.setDominio(dominio);
        Camion actualizado = camionService.actualizar(camion);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{dominio}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable String dominio) {
        camionService.eliminar(dominio);
        return ResponseEntity.noContent().build();
    }
}
