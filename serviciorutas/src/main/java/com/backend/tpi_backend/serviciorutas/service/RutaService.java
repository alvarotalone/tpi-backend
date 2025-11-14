package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.dto.CamionDTO;
import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.repository.RutaRepository;
import com.backend.tpi_backend.serviciorutas.repository.TramoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final RestTemplate restTemplate;

    // URL base del microservicio CAMIONES (configurable en application.yml)
    @Value("${servicios.camiones.url}")
    private String urlServicioCamiones;

    public RutaService(
            RutaRepository rutaRepository,
            TramoRepository tramoRepository,
            RestTemplateBuilder builder) {

        this.rutaRepository = rutaRepository;
        this.tramoRepository = tramoRepository;
        this.restTemplate = builder.build();
    }

    // ============================================================
    // CRUD BÁSICO
    // ============================================================

    /** Lista todas las rutas existentes */
    public List<Ruta> getAll() {
        return rutaRepository.findAll();
    }

    /** Obtiene una ruta por ID, o 404 si no existe */
    public Ruta getById(Long id) {
        return rutaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id " + id));
    }

    /** Crea una ruta nueva */
    public Ruta create(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    /** Actualiza una ruta ya existente */
    public Ruta update(Long id, Ruta updated) {
        Ruta existente = getById(id);
        existente.setCantidadTramos(updated.getCantidadTramos());
        existente.setCantidadDepositos(updated.getCantidadDepositos());
        return rutaRepository.save(existente);
    }

    /** Elimina una ruta */
    public void delete(Long id) {
        rutaRepository.deleteById(id);
    }

    // ============================================================
    // MÉTODOS PARA TRAMOS
    // ============================================================

    /** Obtiene todos los tramos pertenecientes a una ruta */
    public List<Tramo> obtenerTramosDeRuta(Long idRuta) {
        return tramoRepository.findByRuta_Id(idRuta);
    }

    // ============================================================
    // CÁLCULO DE FECHAS ESTIMADAS MIN/MAX
    // ============================================================

    /** 
     * Calcula la fecha estimada de inicio más temprana 
     * entre todos los tramos de una ruta 
     */
    public LocalDateTime calcularFechaMinInicio(List<Tramo> tramos) {
        return tramos.stream()
                .map(Tramo::getFhInicioEstimada)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se pudieron calcular fechas (sin tramos)"));
    }

    /** 
     * Calcula la fecha estimada de fin más tardía 
     * entre todos los tramos de una ruta 
     */
    public LocalDateTime calcularFechaMaxFin(List<Tramo> tramos) {
        return tramos.stream()
                .map(Tramo::getFhFinEstimada)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se pudieron calcular fechas (sin tramos)"));
    }

    // ============================================================
    // MÉTODOS PARA COMUNICARSE CON MICRO SERVICIO CAMIONES
    // ============================================================

    /** 
     * Llama al MS Camiones para validar si un camión puede transportar 
     * un peso y volumen determinados 
     */
    public boolean validarCapacidadCamion(String dominio, double peso, double volumen) {

        String url = urlServicioCamiones + "/" + dominio + "/validar-capacidad";

        Map<String, Double> body = Map.of(
                "peso", peso,
                "volumen", volumen
        );

        ResponseEntity<Boolean> resp = restTemplate.postForEntity(url, body, Boolean.class);

        if (resp.getStatusCode() != HttpStatus.OK || resp.getBody() == null) {
            throw new IllegalStateException("Error al validar capacidad del camión");
        }

        return resp.getBody();
    }

    /** 
     * Llama al MS Camiones para reservar disponibilidad (bloquear fecha) 
     */
    public void reservarDisponibilidadCamion(String dominio, LocalDateTime inicio, LocalDateTime fin) {

        String url = urlServicioCamiones + "/" + dominio + "/disponibilidades";

        Map<String, String> body = Map.of(
                "fechaInicio", inicio.toString(),
                "fechaFin", fin.toString()
        );

        ResponseEntity<Void> resp = restTemplate.postForEntity(url, body, Void.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("No se pudo reservar disponibilidad del camión");
        }
    }

    // ============================================================
    // LÓGICA PRINCIPAL — ASIGNAR CAMIÓN A UNA RUTA
    // ============================================================

    /**
     * Asigna un camión a una ruta:
     *  1. Obtiene la ruta
     *  2. Obtiene los tramos
     *  3. Calcula fechaInicioMin y fechaFinMax
     *  4. Valida capacidad del camión (MS Camiones)
     *  5. Valida disponibilidad (MS Camiones)
     *  6. Reserva disponibilidad
     *  7. Asigna dominio a ruta
     *  8. Asigna dominio a cada tramo
     */
    public Ruta asignarCamionARuta(Long idRuta, String dominioCamion,
                                   double pesoContenedor, double volumenContenedor) {

        Ruta ruta = getById(idRuta);

        List<Tramo> tramos = obtenerTramosDeRuta(idRuta);
        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos, no se puede asignar camión");
        }

        LocalDateTime fechaInicio = calcularFechaMinInicio(tramos);
        LocalDateTime fechaFin = calcularFechaMaxFin(tramos);

        // 1) Validar capacidad
        boolean puedeCargar = validarCapacidadCamion(dominioCamion, pesoContenedor, volumenContenedor);
        if (!puedeCargar) {
            throw new IllegalStateException("El camión no puede transportar el contenedor");
        }

        // 2) Validar disponibilidad → usando GET /camiones/disponibles
        String urlDisponibles = urlServicioCamiones
                + "/disponibles?fechaInicio=" + fechaInicio
                + "&fechaFin=" + fechaFin;

        ResponseEntity<CamionDTO[]> respDisp =
                restTemplate.getForEntity(urlDisponibles, CamionDTO[].class);

        List<CamionDTO> disponibles = respDisp.getBody() != null
                ? Arrays.asList(respDisp.getBody())
                : List.of();

        boolean disponible = disponibles.stream()
                .anyMatch(c -> c.getDominio().equals(dominioCamion));

        if (!disponible) {
            throw new IllegalStateException("El camión no está disponible en ese rango de fechas");
        }

        // 3) Reservar camión
        reservarDisponibilidadCamion(dominioCamion, fechaInicio, fechaFin);

        // 5) Actualizar tramos
        tramos.forEach(t -> t.setDominioCamion(dominioCamion));
        tramoRepository.saveAll(tramos);

        return ruta;
    }
}
