package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.model.TipoTramo;
import com.backend.tpi_backend.serviciorutas.service.TipoTramoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-tramo")
@Tag(name = "Tipos de Tramo", description = "Gestión de tipos de tramo")
public class TipoTramoController {

    private final TipoTramoService tipoTramoService;

    public TipoTramoController(TipoTramoService tipoTramoService) {
        this.tipoTramoService = tipoTramoService;
    }

    @GetMapping
    public List<TipoTramo> listar() {
        return tipoTramoService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoTramo> obtenerPorId(@PathVariable Long id) {
        return tipoTramoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TipoTramo crear(@RequestBody TipoTramo tipoTramo) {
        return tipoTramoService.create(tipoTramo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoTramo> actualizar(@PathVariable Long id, @RequestBody TipoTramo tipoTramo) {
        TipoTramo actualizado = tipoTramoService.update(id, tipoTramo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tipoTramoService.delete(id);
    }
}
