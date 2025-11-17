package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.dto.AsignarCamionRequest;
import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.service.RutaService;
import com.backend.tpi_backend.serviciorutas.service.TramoService;
import com.backend.tpi_backend.serviciorutas.dto.CoordenadasDTO;
import com.backend.tpi_backend.serviciorutas.dto.RutaPosicionDTO;
import com.backend.tpi_backend.serviciorutas.dto.RutaTentativaDTO;
import com.backend.tpi_backend.serviciorutas.dto.TramoDTO;
import com.backend.tpi_backend.serviciorutas.dto.EstadiaCalculoDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
@Tag(name = "Rutas", description = "Gestión de rutas y asignación de camiones")
public class RutaController {

    private final RutaService rutaService;
    private final TramoService tramoService;

    public RutaController(RutaService rutaService, TramoService tramoService) {
        this.rutaService = rutaService;
        this.tramoService = tramoService;
    }

    // ============================================================
    // CRUD BÁSICO
    // ============================================================

    @GetMapping
    public List<Ruta> listar() {
        return rutaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rutaService.getById(id));
    }


    @PostMapping
    public Ruta crear(@RequestBody Ruta ruta) {
        return rutaService.create(ruta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizar(@PathVariable Long id, @RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.update(id, ruta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // AGREGAR TRAMO A UNA RUTA
    // ============================================================

    @PostMapping("/{idRuta}/tramos")
    public ResponseEntity<Tramo> agregarTramo(@PathVariable Long idRuta, @RequestBody Tramo tramo) {
        // Setear la ruta en el tramo
        Ruta ruta = rutaService.getById(idRuta);
        tramo.setRuta(ruta);

        Tramo creado = tramoService.create(tramo);
        return ResponseEntity.ok(creado);
    }

    // ============================================================
    // OBTENER TRAMOS DE UNA RUTA
    // ============================================================

    @GetMapping("/{idRuta}/tramos")
    public ResponseEntity<List<Tramo>> obtenerTramos(@PathVariable Long idRuta) {
        List<Tramo> tramos = tramoService.obtenerTramosPorRuta(idRuta);
        return ResponseEntity.ok(tramos);
    }

    @PutMapping("/{idRuta}/asignar-camion")
    public ResponseEntity<Void> asignarCamion(
            @PathVariable(name="idRuta", required = true) Long idRuta,
            @RequestBody AsignarCamionRequest request) {

        rutaService.asignarCamionARuta(
            idRuta,
            request.getDominioCamion()
        );

        return ResponseEntity.ok().build();
    }


    @GetMapping("/{idRuta}/distancia")
    public ResponseEntity<Double> obtenerDistanciaRuta(@PathVariable Long idRuta) {
        double distancia = rutaService.calcularDistanciaTotalRuta(idRuta);
        return ResponseEntity.ok(distancia / 1000.0);   // en km
    }

    /*
    @PostMapping("/generar-directa")
    public ResponseEntity<RutaTentativaDTO> generarDirecta(@RequestBody CoordenadasDTO dto) {
        return ResponseEntity.ok(rutaService.generarRutaDirecta(dto));
    }
    */

    @PostMapping("/tentativas")
    public ResponseEntity<List<RutaTentativaDTO>> generarTentativas(@RequestBody CoordenadasDTO dto) {
        return ResponseEntity.ok(rutaService.generarTodasLasRutas(dto));
    }

    //=== Obtener ultimo tramo recorrido en la ruta ===
    @PostMapping("/ultima-posicion")
    public ResponseEntity<List<RutaPosicionDTO>> obtenerUltimaPosicion(
            @RequestBody List<Long> idsRuta) {

        return ResponseEntity.ok(
                rutaService.obtenerUltimaPosicionRutas(idsRuta)
        );
    }

    @GetMapping("/{idRuta}/tramos-detallados")
    public ResponseEntity<List<TramoDTO>> obtenerTramosDetallados(@PathVariable Long idRuta) {
        return ResponseEntity.ok(
            rutaService.obtenerTramosDTOConDistancia(idRuta)
        );
    }

    @GetMapping("/{id}/estadias")
    public ResponseEntity<List<EstadiaCalculoDTO>> getEstadias(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.calcularEstadiasRuta(id));
    }

}
