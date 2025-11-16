package com.backend.tpi_backend.servicioclientes.controller;

import com.backend.tpi_backend.servicioclientes.model.Contenedor;
import com.backend.tpi_backend.servicioclientes.model.EstadoContenedor;
import com.backend.tpi_backend.servicioclientes.service.ContenedorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contenedores")
@Tag(name = "Contenedores", description = "Gestión de contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    @GetMapping
    public List<Contenedor> listar() {
        return contenedorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(
            @PathVariable(name = "id", required = true) Long id) {

        return contenedorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public Contenedor crear(@RequestBody Contenedor contenedor) {
        return contenedorService.save(contenedor);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Contenedor> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoContenedor nuevoEstado) {
        Contenedor actualizado = contenedorService.updateEstado(id, nuevoEstado);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        contenedorService.delete(id);
    }
}
