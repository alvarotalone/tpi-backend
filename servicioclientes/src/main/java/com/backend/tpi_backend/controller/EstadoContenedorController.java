package com.backend.tpi_backend.servicioclientes.controller;

import com.backend.tpi_backend.servicioclientes.model.EstadoContenedor;
import com.backend.tpi_backend.servicioclientes.service.EstadoContenedorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados-contenedores")
@Tag(name = "Estados de contenedor", description = "Gestión de estados de contenedores")
public class EstadoContenedorController {

    private final EstadoContenedorService estadoService;

    public EstadoContenedorController(EstadoContenedorService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    public List<EstadoContenedor> listar() {
        return estadoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoContenedor> obtener(@PathVariable Long id) {
        return estadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EstadoContenedor crear(@RequestBody EstadoContenedor estado) {
        return estadoService.save(estado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        estadoService.delete(id);
    }
}