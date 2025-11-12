package com.backend.tpi_backend.serviciodepositos.controller;

import com.backend.tpi_backend.serviciodepositos.model.Provincia;
import com.backend.tpi_backend.serviciodepositos.service.ProvinciaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provincias")
@Tag(name = "Provincias", description = "Gestión de provincias")
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    public ProvinciaController(ProvinciaService provinciaService) {
        this.provinciaService = provinciaService;
    }

    @GetMapping
    public List<Provincia> listar() {
        return provinciaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Provincia> obtenerPorId(@PathVariable Long id) {
        Provincia provincia = provinciaService.buscarPorId(id);
        if (provincia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(provincia);
    }

    @PostMapping
    public Provincia crear(@RequestBody Provincia provincia) {
        return provinciaService.crear(provincia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Provincia> actualizar(@PathVariable Long id, @RequestBody Provincia provincia) {
        Provincia actualizado = provinciaService.actualizar(id, provincia);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        provinciaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}