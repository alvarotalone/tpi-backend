package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.EstadoTramo;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.repository.EstadoTramoRepository;
import com.backend.tpi_backend.serviciorutas.repository.TramoRepository;

import jakarta.persistence.EntityNotFoundException;

import com.backend.tpi_backend.serviciorutas.dto.CamionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TramoService {

    private final TramoRepository repository;
    private final RestTemplate restTemplate;
    private final EstadoTramoRepository estadoTramoRepository;

    @Value("${servicios.camiones.url}")
    private String urlServicioCamiones;

    public TramoService(TramoRepository repository, RestTemplateBuilder builder, EstadoTramoRepository estadoTramoRepository) {
        this.repository = repository;
        this.restTemplate = builder.build();
        this.estadoTramoRepository = estadoTramoRepository;
    }

    public List<Tramo> getAll() { return repository.findAll(); }

    public Optional<Tramo> getById(Long id) { return repository.findById(id); }

    public Tramo create(Tramo tramo) { return repository.save(tramo); }

    public Tramo update(Long id, Tramo updated) {
        return repository.findById(id).map(t -> {
            updated.setId(id);
            return repository.save(updated);
        }).orElseThrow(() -> new RuntimeException("Tramo no encontrado con id " + id));
    }

    public void delete(Long id) { repository.deleteById(id); }

    public Tramo asignarCamionATramo(Long idTramo, String dominioCamion) {
        // Buscar tramo existente
        Tramo tramo = repository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        // Consultar servicio de camiones
        CamionDTO camion = restTemplate.getForObject(
                urlServicioCamiones + "/" + dominioCamion,
                CamionDTO.class
        );

        if (camion == null) {
            throw new RuntimeException("Camión no encontrado en servicio de camiones");
        }

        if (!camion.isDisponible()) {
            throw new RuntimeException("El camión no está disponible");
        }

        // Asignar camión al tramo
        tramo.setDominioCamion(dominioCamion);
        // Actualizar el estado del tramo a “ASIGNADO”
        EstadoTramo estadoAsignado = estadoTramoRepository.findByDescripcion("Asignado")
                .orElseThrow(() -> new RuntimeException("Estado 'ASIGNADO' no encontrado"));
        tramo.setEstadoTramo(estadoAsignado);
        repository.save(tramo);

        // Notificar a servicio de camiones que ya no está disponible
        restTemplate.put(
                urlServicioCamiones + "/" + dominioCamion + "/disponibilidad",
                Map.of("disponible", false)
        );

        return tramo;
    }

    public void iniciarTramo(Long idTramo) {
        Tramo tramo = repository.findById(idTramo)
            .orElseThrow(() -> new EntityNotFoundException("Tramo no encontrado"));

        // Verificar que el tramo tenga camión asignado
        if (tramo.getDominioCamion() == null)
            throw new IllegalStateException("El tramo no tiene un camión asignado");

        // Actualizar fecha y estado
        tramo.setFhInicioReal(LocalDateTime.now());

        EstadoTramo estadoIniciado = estadoTramoRepository.findByDescripcion("En curso")
            .orElseThrow(() -> new EntityNotFoundException("Estado En Curso no definido"));
        tramo.setEstadoTramo(estadoIniciado);

        repository.save(tramo);
    }

    public void finalizarTramo(Long idTramo) {
        Tramo tramo = repository.findById(idTramo)
            .orElseThrow(() -> new EntityNotFoundException("Tramo no encontrado"));

        if (tramo.getDominioCamion() == null)
            throw new IllegalStateException("El tramo no tiene un camión asignado");

        tramo.setFhFinReal(LocalDateTime.now());

        // Cambiar estado
        EstadoTramo estadoFinalizado = estadoTramoRepository.findByDescripcion("Finalizado")
            .orElseThrow(() -> new EntityNotFoundException("Estado FINALIZADO no definido"));
        tramo.setEstadoTramo(estadoFinalizado);

        // Liberar camión para poder volver a usarlo
        Map<String, Boolean> body = Map.of("disponible", true);
        restTemplate.put(
            urlServicioCamiones + "/" + tramo.getDominioCamion() + "/disponibilidad",
            body
        );


        repository.save(tramo);
    }

}
