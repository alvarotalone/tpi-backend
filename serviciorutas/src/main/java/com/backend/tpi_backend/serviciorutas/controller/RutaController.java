package com.backend.tpi_backend.serviciorutas.controller;

import com.backend.tpi_backend.serviciorutas.dto.AsignarCamionRequest;
import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.service.RutaService;
import com.backend.tpi_backend.serviciorutas.service.TramoService;
import com.backend.tpi_backend.serviciorutas.dto.CoordenadasDTO;
import com.backend.tpi_backend.serviciorutas.dto.RutaTentativaDTO;

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
    public ResponseEntity<Ruta> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rutaService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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

    // ============================================================
    // ASIGNAR CAMIÓN A UNA RUTA (**TEMPORAL** hasta integrar con Solicitud)
    // ============================================================

    /**
     * Endpoint temporal.
     * Solicitud va a usar esta operación internamente más adelante.
     * 
     * JSON esperado:
     * {
     *   "dominioCamion": "AA123BB",
     *   "pesoContenedor": 2000,
     *   "volumenContenedor": 12
     * }
     */
    @PutMapping("/{idRuta}/asignar-camion")
    public ResponseEntity<Ruta> asignarCamion(
            @PathVariable Long idRuta,
            @RequestBody AsignarCamionRequest request) {

        Ruta ruta = rutaService.asignarCamionARuta(
                        idRuta,
                        request.getDominioCamion(),
                        request.getPesoContenedor(),
                        request.getVolumenContenedor()
                );

        return ResponseEntity.ok(ruta);
    }

    @GetMapping("/{idRuta}/distancia")
    public ResponseEntity<Double> obtenerDistanciaRuta(@PathVariable Long idRuta) {
        double distancia = rutaService.calcularDistanciaTotalRuta(idRuta);
        return ResponseEntity.ok(distancia / 1000.0);   // en km
    }

    @PostMapping("/generar-directa")
    public ResponseEntity<RutaTentativaDTO> generarDirecta(@RequestBody CoordenadasDTO dto) {
        return ResponseEntity.ok(rutaService.generarRutaDirecta(dto));
    }

    @PostMapping("/generar-norte")
    public ResponseEntity<RutaTentativaDTO> generarNorte(@RequestBody CoordenadasDTO dto) {
        return ResponseEntity.ok(rutaService.generarRutaNorte(dto));
    }

    @PostMapping("/generar-este")
    public ResponseEntity<RutaTentativaDTO> generarEste(@RequestBody CoordenadasDTO dto) {
        return ResponseEntity.ok(rutaService.generarRutaEste(dto));
    }

}
