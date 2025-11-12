package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.model.EstadoTramo;
import com.backend.tpi_backend.serviciorutas.service.EstadoTramoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados-tramo")
@Tag(name = "Estados de Tramo", description = "Gestión de estados de tramo")
public class EstadoTramoController {

    private final EstadoTramoService estadoTramoService;

    public EstadoTramoController(EstadoTramoService estadoTramoService) {
        this.estadoTramoService = estadoTramoService;
    }

    @GetMapping
    public List<EstadoTramo> listar() {
        return estadoTramoService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoTramo> obtenerPorId(@PathVariable Long id) {
        return estadoTramoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EstadoTramo crear(@RequestBody EstadoTramo estadoTramo) {
        return estadoTramoService.create(estadoTramo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoTramo> actualizar(@PathVariable Long id, @RequestBody EstadoTramo estadoTramo) {
        EstadoTramo actualizado = estadoTramoService.update(id, estadoTramo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        estadoTramoService.delete(id);
    }
}
