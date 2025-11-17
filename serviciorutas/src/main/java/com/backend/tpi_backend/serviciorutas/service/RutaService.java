package com.backend.tpi_backend.serviciorutas.service;


import com.backend.tpi_backend.serviciorutas.dto.CoordenadasDTO;
import com.backend.tpi_backend.serviciorutas.dto.DepositoDTO;
import com.backend.tpi_backend.serviciorutas.dto.RutaPosicionDTO;
import com.backend.tpi_backend.serviciorutas.model.Ruta;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.repository.EstadoTramoRepository;
import com.backend.tpi_backend.serviciorutas.repository.RutaRepository;
import com.backend.tpi_backend.serviciorutas.repository.TipoTramoRepository;
import com.backend.tpi_backend.serviciorutas.repository.TramoRepository;
import com.backend.tpi_backend.serviciorutas.dto.RutaTentativaDTO;
import com.backend.tpi_backend.serviciorutas.client.DepositoClient;
import com.backend.tpi_backend.serviciorutas.dto.TramoDTO;
import com.backend.tpi_backend.serviciorutas.dto.EstadiaCalculoDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final RestTemplate restTemplate;
    private final OsrmClient osrmClient;
    private final EstadoTramoRepository estadoTramoRepository;
    private final TipoTramoRepository tipoTramoRepository;
    private final DepositoClient depositoClient;


    // URL base del microservicio CAMIONES (configurable en application.yml)
    @Value("${servicios.camiones.url}")
    private String urlServicioCamiones;

    public RutaService(
            RutaRepository rutaRepository,
            TramoRepository tramoRepository,
            EstadoTramoRepository estadoTramoRepository,
            TipoTramoRepository tipoTramoRepository,
            RestTemplateBuilder builder,
            OsrmClient osrmClient,
            DepositoClient depositoClient) {

        this.rutaRepository = rutaRepository;
        this.tramoRepository = tramoRepository;
        this.estadoTramoRepository = estadoTramoRepository;
        this.tipoTramoRepository = tipoTramoRepository;
        this.restTemplate = builder.build();
        this.osrmClient = osrmClient;
        this.depositoClient = depositoClient;
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
        existente.setDuracionEstimada(updated.getDuracionEstimada());
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

    

    public void asignarCamionARuta(Long idRuta, String dominioCamion) {

        if (!rutaRepository.existsById(idRuta)) {
            throw new IllegalStateException("La ruta no existe");
        }

        List<Tramo> tramos = obtenerTramosDeRuta(idRuta);

        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos");
        }

        tramos.forEach(t -> t.setDominioCamion(dominioCamion));

        tramoRepository.saveAll(tramos);
    }

    public double calcularDistanciaTramo(Tramo tramo) {

        var resultado = osrmClient.obtenerRuta(
                tramo.getLatitudOrigen(),
                tramo.getLongitudOrigen(),
                tramo.getLatitudDestino(),
                tramo.getLongitudDestino()
        );

        return resultado.getDistance();  // distancia en metros
    }

    /**
     * Calcula la distancia TOTAL (suma) de todos los tramos de una ruta.
     */
    public double calcularDistanciaTotalRuta(Long idRuta) {

        List<Tramo> tramos = obtenerTramosDeRuta(idRuta);

        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos");
        }

        return tramos.stream()
                .mapToDouble(this::calcularDistanciaTramo)
                .sum();
    }

    public RutaTentativaDTO generarRutaDirecta(CoordenadasDTO dto) {

        var rutaOSRM = osrmClient.obtenerRuta(
                dto.getLatO(), dto.getLonO(),
                dto.getLatD(), dto.getLonD()
        );

        Ruta ruta = new Ruta();
        ruta.setCantidadTramos(1);
        ruta.setCantidadDepositos(0);
        ruta.setDuracionEstimada( (int) rutaOSRM.getDuration() );
        ruta = rutaRepository.save(ruta);

        Tramo t = new Tramo();
        t.setRuta(ruta);
        t.setLatitudOrigen(dto.getLatO());
        t.setLongitudOrigen(dto.getLonO());
        t.setLatitudDestino(dto.getLatD());
        t.setLongitudDestino(dto.getLonD());
        t.setEstadoTramo(estadoTramoRepository.findByDescripcion("Pendiente").orElseThrow());
        t.setTipoTramo(tipoTramoRepository.findById(1L).orElseThrow());

        tramoRepository.save(t);

        RutaTentativaDTO resp = new RutaTentativaDTO();
        resp.setIdRuta(ruta.getId());
        resp.setDistancia(rutaOSRM.getDistance());
        resp.setDuracion(rutaOSRM.getDuration());
        resp.setDescripcion("Ruta directa");

        return resp;
    }

    public List<RutaTentativaDTO> generarRutasConDepositos(CoordenadasDTO dto) {

        List<DepositoDTO> depositos = depositoClient.listarDepositos();

        List<RutaTentativaDTO> rutas = new ArrayList<>();

        for (DepositoDTO dep : depositos) {

            double latDep = dep.getUbicacion().getLatitud();
            double lonDep = dep.getUbicacion().getLongitud();

            // === OSRM: origen → depósito
            var tramo1 = osrmClient.obtenerRuta(
                    dto.getLatO(), dto.getLonO(),
                    latDep, lonDep
            );

            // === OSRM: depósito → destino
            var tramo2 = osrmClient.obtenerRuta(
                    latDep, lonDep,
                    dto.getLatD(), dto.getLonD()
            );

            double distanciaTotal = tramo1.getDistance() + tramo2.getDistance();
            double duracionTotal = tramo1.getDuration() + tramo2.getDuration();

            // === Crear Ruta ===
            Ruta ruta = new Ruta();
            ruta.setCantidadTramos(2);
            ruta.setCantidadDepositos(1);
            ruta.setDuracionEstimada((int) duracionTotal);
            ruta = rutaRepository.save(ruta);

            // === Crear tramo 1: origen → depósito
            Tramo t1 = new Tramo();
            t1.setRuta(ruta);
            t1.setLatitudOrigen(dto.getLatO());
            t1.setLongitudOrigen(dto.getLonO());
            t1.setLatitudDestino(latDep);
            t1.setLongitudDestino(lonDep);
            t1.setEstadoTramo(estadoTramoRepository.findByDescripcion("Pendiente").orElseThrow());
            t1.setTipoTramo(tipoTramoRepository.findByDescripcion("origen-deposito").orElseThrow());
            tramoRepository.save(t1);

            // === Crear tramo 2: depósito → destino
            Tramo t2 = new Tramo();
            t2.setRuta(ruta);
            t2.setLatitudOrigen(latDep);
            t2.setLongitudOrigen(lonDep);
            t2.setLatitudDestino(dto.getLatD());
            t2.setLongitudDestino(dto.getLonD());
            t2.setEstadoTramo(estadoTramoRepository.findByDescripcion("Pendiente").orElseThrow());
            t2.setTipoTramo(tipoTramoRepository.findByDescripcion("deposito-destino").orElseThrow());
            tramoRepository.save(t2);

            // === Crear el DTO de respuesta ===
            RutaTentativaDTO r = new RutaTentativaDTO();
            r.setIdRuta(ruta.getId());
            r.setDistancia(distanciaTotal);
            r.setDuracion(duracionTotal);
            r.setDescripcion("Ruta pasando por depósito: " + dep.getNombre());

            rutas.add(r);
        }

        return rutas;
    }

    public List<RutaTentativaDTO> generarTodasLasRutas(CoordenadasDTO dto) {

        List<RutaTentativaDTO> resultados = new ArrayList<>();

        // 1) Ruta directa
        RutaTentativaDTO directa = generarRutaDirecta(dto);
        resultados.add(directa);

        // 2) Rutas pasando por depósitos
        List<RutaTentativaDTO> conDepositos = generarRutasConDepositos(dto);
        resultados.addAll(conDepositos);

        return resultados;
    }

    //==== Obtener ultimo tramo recorrido de la ruta  ====
    public List<RutaPosicionDTO> obtenerUltimaPosicionRutas(List<Long> idsRuta) {
        if (idsRuta == null || idsRuta.isEmpty()) {
            return Collections.emptyList();
        }

        List<RutaPosicionDTO> resultado = new ArrayList<>();

        for (Long idRuta : idsRuta) {

            // todos los tramos de esa ruta
            List<Tramo> tramos = tramoRepository.findByRuta_Id(idRuta);

            if (tramos.isEmpty()) {
                // si querés ignorar rutas sin tramos, podés hacer simplemente "continue;"
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No hay tramos para la ruta " + idRuta
                );
            }

            // tramo con fh_fin_real más grande
            Tramo ultimoTramo = tramos.stream()
                    .filter(t -> t.getFhFinReal() != null)
                    .max(Comparator.comparing(Tramo::getFhFinReal))
                    .orElse(null);

            if (ultimoTramo == null) {
                // no hay ningún tramo con fh_fin_real cargada → la salteamos
                continue;
            }

            RutaPosicionDTO dto = new RutaPosicionDTO();
            dto.setIdRuta(idRuta);
            dto.setLatitudDestino(ultimoTramo.getLatitudDestino());
            dto.setLongitudDestino(ultimoTramo.getLongitudDestino());

            resultado.add(dto);
        }

        return resultado;
    }

    /**
     * Devuelve los tramos de una ruta con la distancia calculada por OSRM.
     */
    public List<TramoDTO> obtenerTramosDTOConDistancia(Long idRuta) {

        List<Tramo> tramos = obtenerTramosDeRuta(idRuta);
        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene tramos");
        }

        List<TramoDTO> dtos = new ArrayList<>();

        for (Tramo t : tramos) {
            TramoDTO dto = new TramoDTO();
            dto.setId(t.getId());
            dto.setLatitudOrigen(t.getLatitudOrigen());
            dto.setLongitudOrigen(t.getLongitudOrigen());
            dto.setLatitudDestino(t.getLatitudDestino());
            dto.setLongitudDestino(t.getLongitudDestino());
            dto.setDominioCamion(t.getDominioCamion());
            dto.setFhInicioReal(t.getFhInicioReal());
            dto.setFhFinReal(t.getFhFinReal());

            dto.setTipoTramo(
                    t.getTipoTramo() != null ? t.getTipoTramo().getDescripcion() : null
            );
            dto.setEstadoTramo(
                    t.getEstadoTramo() != null ? t.getEstadoTramo().getDescripcion() : null
            );

            // Distancia en metros usando OSRM
            double distancia = calcularDistanciaTramo(t); // ya lo tenés implementado
            dto.setDistanciaMetros(distancia);

            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * Calcula las estadías por depósito para una ruta.
     * Supone que:
     * - los tramos que llegan/salen de un depósito tienen idDeposito seteado
     * - fhInicioReal / fhFinReal están cargados
     */
    public List<EstadiaCalculoDTO> calcularEstadiasRuta(Long idRuta) {

        List<Tramo> tramos = obtenerTramosDeRuta(idRuta);

        // Si no tenés estadías todavía, podés devolver directamente List.of()
        // return List.of();

        // Filtrar solo tramos con depósito asociado
        List<Tramo> conDeposito = tramos.stream()
                .filter(t -> t.getIdDeposito() != null)
                .toList();

        List<EstadiaCalculoDTO> resultado = new ArrayList<>();

        // Agrupamos por idDeposito
        Map<Long, List<Tramo>> porDeposito = conDeposito.stream()
                .collect(Collectors.groupingBy(Tramo::getIdDeposito));

        for (Map.Entry<Long, List<Tramo>> entry : porDeposito.entrySet()) {

            Long idDeposito = entry.getKey();
            List<Tramo> tramosDep = entry.getValue();

            // tramo de llegada = el que tiene menor fhInicioReal
            // tramo de salida = el que tiene mayor fhFinReal
            Tramo llegada = tramosDep.stream()
                    .filter(t -> t.getFhFinReal() != null)
                    .min(Comparator.comparing(Tramo::getFhFinReal))
                    .orElse(null);

            Tramo salida = tramosDep.stream()
                    .filter(t -> t.getFhInicioReal() != null)
                    .max(Comparator.comparing(Tramo::getFhInicioReal))
                    .orElse(null);

            if (llegada == null || salida == null) {
                continue; // aún no termina el ciclo por ese depósito
            }

            EstadiaCalculoDTO e = new EstadiaCalculoDTO();
            e.setIdDeposito(idDeposito);
            e.setFechaInicio(llegada.getFhFinReal().toLocalDate());
            e.setFechaFin(salida.getFhInicioReal().toLocalDate());

            resultado.add(e);
        }

        return resultado;
    }
}
