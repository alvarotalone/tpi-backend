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

    @GetMapping
    public List<Solicitud> listar() { return service.findAll(); }

    @GetMapping("/{numero}")
    public ResponseEntity<Solicitud> obtener(@PathVariable Long numero) {
        return service.findByNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Solicitud crear(@RequestBody Solicitud s) { return service.save(s); }

    @PutMapping("/{numero}/estado")
    public ResponseEntity<Solicitud> actualizarEstado(
            @PathVariable Long numero,
            @RequestParam Long idEstado) {
        Solicitud actualizado = service.updateEstado(numero, idEstado);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{numero}")
    public void eliminar(@PathVariable Long numero) { service.delete(numero); }
}
