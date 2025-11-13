package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.service.TramoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.tpi_backend.serviciorutas.dto.AsignarCamionRequest;

import java.util.List;

@RestController
@RequestMapping("/tramos")
@Tag(name = "Tramos", description = "Gestión de tramos de ruta")
public class TramoController {

    private final TramoService tramoService;

    public TramoController(TramoService tramoService) {
        this.tramoService = tramoService;
    }

    @GetMapping
    public List<Tramo> listar() {
        return tramoService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable Long id) {
        return tramoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tramo crear(@RequestBody Tramo tramo) {
        return tramoService.create(tramo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramo> actualizar(@PathVariable Long id, @RequestBody Tramo tramo) {
        Tramo actualizado = tramoService.update(id, tramo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tramoService.delete(id);
    }

    // Endpoint para asignar el camion a cada tramo
    @PutMapping("/{id}/asignar-camion")
    public ResponseEntity<Tramo> asignarCamionATramo(
            @PathVariable Long id,
            @RequestBody AsignarCamionRequest request) {

        Tramo tramo = tramoService.asignarCamionATramo(id, request.getDominioCamion());
        return ResponseEntity.ok(tramo);
    }

    @PutMapping("/tramos/{id}/iniciar")
    public ResponseEntity<String> iniciarTramo(@PathVariable Long id) {
        tramoService.iniciarTramo(id);
        return ResponseEntity.ok("Tramo iniciado correctamente");
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<String> finalizarTramo(@PathVariable Long id) {
        tramoService.finalizarTramo(id);
        return ResponseEntity.ok("Tramo finalizado correctamente");
    }
}
