package com.backend.tpi_backend.serviciocamiones.controller;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.model.DetalleDisponibilidad;
import com.backend.tpi_backend.serviciocamiones.service.CamionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/camiones")
@Tag(name = "Camiones", description = "Gestión de camiones y disponibilidad")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping
    public List<Camion> listarCamiones() {
        return camionService.obtenerTodos();
    }

    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> obtenerCamion(@PathVariable String dominio) {
        return camionService.obtenerPorDominio(dominio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camion> crearCamion(@RequestBody Camion camion) {
        Camion nuevo = camionService.guardar(camion);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{dominio}")
    public ResponseEntity<Camion> actualizarCamion(@PathVariable String dominio, @RequestBody Camion camion) {
        camion.setDominio(dominio);
        Camion actualizado = camionService.actualizar(camion);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{dominio}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable String dominio) {
        camionService.eliminar(dominio);
        return ResponseEntity.noContent().build();
    }

    //=== Validar capacidad maxima ====
    @PostMapping("/{dominio}/validar-capacidad")
    public ResponseEntity<Boolean> validarCapacidad(
            @PathVariable String dominio,
            @RequestBody Map<String, Double> body) {

        if (body == null || !body.containsKey("peso") || !body.containsKey("volumen")) {
            return ResponseEntity.badRequest().build();
        }

        Double peso = body.get("peso");
        Double volumen = body.get("volumen");

        if (peso == null || volumen == null || peso <= 0 || volumen <= 0) {
            return ResponseEntity.badRequest().build();
        }

        boolean puede = camionService.puedeTransportar(dominio, peso, volumen);
        return ResponseEntity.ok(puede);
    }

    // === Obtener camiones disponibles en un rango de fechas y un peso/volumen minimo ===
    @GetMapping("/disponibles")
    public ResponseEntity<List<Camion>> obtenerCamionesDisponibles(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Double peso,
            @RequestParam(required = false) Double volumen) {

        List<Camion> resultado;

        if (peso == null && volumen == null) {
            // Uso simple: solo ver quién está libre en esas fechas
            resultado = camionService.obtenerCamionesDisponibles(fechaInicio, fechaFin);

        } else if (peso == null || volumen == null) {
            // Vino uno solo: eso es un uso incorrecto de la API
            return ResponseEntity.badRequest().build();

        } else {
            // Caso principal para servicio rutas: fecha + capacidad
            resultado = camionService.obtenerCamionesDisponibles(
                    fechaInicio, fechaFin, peso, volumen);
        }

        return ResponseEntity.ok(resultado);
    }



    //=== Reservar un camion (marcarlo ocupado segun la ruta que se le asigno) ===
    @PostMapping("/{dominio}/disponibilidades")
    public ResponseEntity<DetalleDisponibilidad> reservarCamion(
            @PathVariable String dominio,
            @RequestBody Map<String, String> body) {

        String fechaInicio = body.get("fechaInicio");
        String fechaFin = body.get("fechaFin");

        if (fechaInicio == null || fechaFin == null) {
            return ResponseEntity.badRequest().build();
        }

        DetalleDisponibilidad detalle = camionService.reservarCamion(dominio, fechaInicio, fechaFin);

        return ResponseEntity.ok(detalle);
    }
}
