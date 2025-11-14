package com.backend.tpi_backend.serviciodepositos.controller;

import com.backend.tpi_backend.serviciodepositos.model.Ubicacion;
import com.backend.tpi_backend.serviciodepositos.service.UbicacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones")
@Tag(name = "Ubicaciones", description = "Gestión de ubicaciones (lat/long)")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    public List<Ubicacion> listar() {
        return ubicacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> obtenerPorId(@PathVariable("id") Long id) {
        Ubicacion ubicacion = ubicacionService.buscarPorId(id);
        if (ubicacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ubicacion);
    }

    @PostMapping
    public Ubicacion crear(@RequestBody Ubicacion ubicacion) {
        return ubicacionService.crear(ubicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizar(@PathVariable Long id, @RequestBody Ubicacion ubicacion) {
        Ubicacion actualizado = ubicacionService.actualizar(id, ubicacion);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}