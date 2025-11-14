package com.backend.tpi_backend.serviciotarifas.controller;

import com.backend.tpi_backend.serviciotarifas.dto.CostoTotalDTO; 
import com.backend.tpi_backend.serviciotarifas.dto.CostoTramoDTO; 
import com.backend.tpi_backend.serviciotarifas.service.TarifaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import java.time.LocalDate;

@RestController
@RequestMapping("/api/costos") // Nueva ruta base para cálculos
public class CostoController {

    private final TarifaService tarifaService;

    public CostoController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    /**
     * Endpoint para Calcular costo de estadía en depósito. el endpoint toma los 3 paramentros necesarios. 
     */
    @GetMapping("/estadia")
    public ResponseEntity<?> getCostoEstadia(
            @RequestParam Long idDeposito,
            @RequestParam String fechaInicio, // Recibimos como String
            @RequestParam String fechaFin) {   // Recibimos como String

        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            
            //  llamo al metodo que defini
            Double costo = tarifaService.calcularCostoEstadia(idDeposito, inicio, fin);
            
            return ResponseEntity.ok(costo); // 200 OK con el costo (ej: 4500.0)
        
        } catch (RuntimeException e) {
            // Si el depósito no se encuentra (desde TarifaService)
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // Si las fechas están mal formateadas
            return ResponseEntity.badRequest().body("Error en los parámetros: " + e.getMessage());
        }
    }

    // Endpoint para calcular costo por tramo individual.
    @PostMapping("/tramo") // uso POST porque estamos enviando un cuerpo (el JSON del DTO) con la solicitud. es mas comodo que un GET con 5 parametros. 
    public ResponseEntity<?> getCostoTramo(@RequestBody CostoTramoDTO dto) { 
        // RequestBody le dice a spring que tome el JSON del body y lo convierta en objeto CostoTramoDTO
        try {
            Double costo = tarifaService.calcularCostoTramo(dto); // llamo al metodo definido
            return ResponseEntity.ok(costo); // 200 OK con el costo
         
        // manejo de rrores    
        } catch (RuntimeException e) {
            // Si no se encuentra una tarifa aplicable (desde TarifaService)
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en los parámetros de entrada: " + e.getMessage());
        }
    }

    @PostMapping("/calcular")
    public ResponseEntity<?> getCostoTotal(@RequestBody CostoTotalDTO request) {
        try {
            Double costoTotal = tarifaService.calcularCostoTotal(request);
            return ResponseEntity.ok(costoTotal); // 200 OK con el costo total

        } catch (RuntimeException e) {
            // Si no se encuentra una tarifa o un depósito
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en los parámetros de entrada: " + e.getMessage());
        }
    }
}