package com.backend.tpi_backend.serviciorutas.service;

import com.backend.tpi_backend.serviciorutas.model.EstadoTramo;
import com.backend.tpi_backend.serviciorutas.model.Tramo;
import com.backend.tpi_backend.serviciorutas.repository.EstadoTramoRepository;
import com.backend.tpi_backend.serviciorutas.repository.TramoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TramoService {

    private final TramoRepository repository;
    private final EstadoTramoRepository estadoTramoRepository;

    @Value("${servicios.camiones.url}")
    private String urlServicioCamiones;

    public TramoService(TramoRepository repository, EstadoTramoRepository estadoTramoRepository) {
        this.repository = repository;
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

    // ============================================================
    // ASIGNAR CAMIÓN A **TODOS** LOS TRAMOS DE UNA RUTA
    // (usado por RutaService.asignarCamionARuta)
    // ============================================================

    public void asignarCamionATodosLosTramos(Long idRuta, String dominioCamion) {
        List<Tramo> tramos = repository.findByRuta_Id(idRuta);

        if (tramos.isEmpty())
            throw new IllegalStateException("La ruta no tiene tramos");

        tramos.forEach(t -> t.setDominioCamion(dominioCamion));
        repository.saveAll(tramos);
    }


    // INICIAR UN TRAMO (TRANSPORTISTA)
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

    public List<Tramo> obtenerTramosPorRuta(Long idRuta) {
        return repository.findByRuta_Id(idRuta);
    }


    // FINALIZAR UN TRAMO (TRANSPORTISTA)
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

        

        /* 
        // Liberar camión para poder volver a usarlo
        Map<String, Boolean> body = Map.of("disponible", true);
        restTemplate.put(
            urlServicioCamiones + "/" + tramo.getDominioCamion() + "/disponibilidad",
            body
        );
        */


        repository.save(tramo);
    }

}
