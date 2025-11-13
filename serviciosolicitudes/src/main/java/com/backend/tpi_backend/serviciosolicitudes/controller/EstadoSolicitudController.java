package com.backend.tpi_backend.serviciosolicitudes.controller;

import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import com.backend.tpi_backend.serviciosolicitudes.service.EstadoSolicitudService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados-solicitudes")
@Tag(name = "Estados de solicitud", description = "Gestión de estados de solicitud")
public class EstadoSolicitudController {

    private final EstadoSolicitudService service;

    public EstadoSolicitudController(EstadoSolicitudService service) {
        this.service = service;
    }

    @GetMapping
    public List<EstadoSolicitud> listar() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoSolicitud> obtener(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EstadoSolicitud crear(@RequestBody EstadoSolicitud estado) {
        return service.save(estado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.delete(id); }

    

}
