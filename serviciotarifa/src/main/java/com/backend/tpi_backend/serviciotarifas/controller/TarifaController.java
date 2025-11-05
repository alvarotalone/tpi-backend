package com.backend.tpi_backend.serviciotarifas.controller;

import com.backend.tpi_backend.serviciotarifas.model.Tarifa;
import com.backend.tpi_backend.serviciotarifas.service.TarifaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping
    public ResponseEntity<List<Tarifa>> getAll() {
        return ResponseEntity.ok(tarifaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getById(@PathVariable Long id) {
        return tarifaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tarifa> create(@RequestBody Tarifa tarifa) {
        return ResponseEntity.ok(tarifaService.create(tarifa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> update(@PathVariable Long id, @RequestBody Tarifa tarifa) {
        return ResponseEntity.ok(tarifaService.update(id, tarifa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarifaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint adicional: listar tarifas vigentes en una fecha específica
    @GetMapping("/vigentes")
    public ResponseEntity<List<Tarifa>> getTarifasVigentes(@RequestParam("fecha") String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        return ResponseEntity.ok(tarifaService.getTarifasVigentes(fecha));
    }
}
