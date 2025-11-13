package com.backend.tpi_backend.serviciosolicitudes.controller;

import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import com.backend.tpi_backend.serviciosolicitudes.service.SolicitudService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@Tag(name = "Solicitudes", description = "Gestión de solicitudes de transporte de contenedor")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    // 🔹 Listar todas las solicitudes
    @GetMapping
    public List<Solicitud> listar() {
        return service.findAll();
    }

    // 🔹 Obtener una solicitud por ID
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear una nueva solicitud
    @PostMapping
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return service.save(solicitud);
    }

    // 🔹 Actualizar el estado de una solicitud
    @PutMapping("/{id}/estado")
    public ResponseEntity<Solicitud> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Long idEstado) {
        Solicitud actualizado = service.updateEstado(id, idEstado);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    // 🔹 Eliminar una solicitud
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.delete(id);
    }
}
