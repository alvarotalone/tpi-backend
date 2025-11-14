package com.backend.tpi_backend.serviciodepositos.controller;

import com.backend.tpi_backend.serviciodepositos.model.Ciudad;
import com.backend.tpi_backend.serviciodepositos.service.CiudadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciudades")
@Tag(name = "Ciudades", description = "Gestión de ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    public CiudadController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @GetMapping
    public List<Ciudad> listar() {
        return ciudadService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerPorId(@PathVariable("id") Long id) {
        Ciudad ciudad = ciudadService.buscarPorId(id);
        if (ciudad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ciudad);
    }

    @PostMapping
    public Ciudad crear(@RequestBody Ciudad ciudad) {
        return ciudadService.crear(ciudad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> actualizar(@PathVariable Long id, @RequestBody Ciudad ciudad) {
        Ciudad actualizado = ciudadService.actualizar(id, ciudad);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ciudadService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}