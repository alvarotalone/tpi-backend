package com.backend.tpi_backend.serviciocamiones.controller;

import com.backend.tpi_backend.serviciocamiones.model.Transportista;
import com.backend.tpi_backend.serviciocamiones.service.TransportistaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transportistas")
@Tag(name = "Transportistas", description = "Gestión de transportistas y asignación de camiones")
public class TransportistaController {

    private final TransportistaService transportistaService;

    public TransportistaController(TransportistaService transportistaService) {
        this.transportistaService = transportistaService;
    }

    @GetMapping
    public List<Transportista> listarTransportistas() {
        return transportistaService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerTransportista(@PathVariable Long id) {
        return transportistaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transportista> crearTransportista(@RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.guardar(transportista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> actualizarTransportista(@PathVariable Long id, @RequestBody Transportista transportista) {
        transportista.setId(id);
        return ResponseEntity.ok(transportistaService.actualizar(transportista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransportista(@PathVariable Long id) {
        transportistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
