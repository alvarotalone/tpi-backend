package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.service.RutaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
@Tag(name = "Rutas", description = "Gestión de rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping
    public List<Ruta> listar() {
        return rutaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerPorId(@PathVariable Long id) {
        return rutaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ruta crear(@RequestBody Ruta ruta) {
        return rutaService.create(ruta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizar(@PathVariable Long id, @RequestBody Ruta ruta) {
        Ruta actualizada = rutaService.update(id, ruta);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rutaService.delete(id);
    }
}
