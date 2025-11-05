package com.backend.tpi_backend.serviciocamiones.controller;

import com.backend.tpi_backend.serviciocamiones.model.TipoCamion;
import com.backend.tpi_backend.serviciocamiones.service.TipoCamionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-camion")
@Tag(name = "Tipos de Camión", description = "Gestión de tipos de camiones")
public class TipoCamionController {

    private final TipoCamionService tipoCamionService;

    public TipoCamionController(TipoCamionService tipoCamionService) {
        this.tipoCamionService = tipoCamionService;
    }

    @GetMapping
    public List<TipoCamion> listarTipos() {
        return tipoCamionService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCamion> obtenerTipo(@PathVariable Long id) {
        return tipoCamionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoCamion> crearTipo(@RequestBody TipoCamion tipo) {
        return ResponseEntity.ok(tipoCamionService.guardar(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCamion> actualizarTipo(@PathVariable Long id, @RequestBody TipoCamion tipo) {
        tipo.setId(id);
        return ResponseEntity.ok(tipoCamionService.actualizar(tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipo(@PathVariable Long id) {
        tipoCamionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
