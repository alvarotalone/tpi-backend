package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.repository.CamionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CamionService {

    private final CamionRepository camionRepository;

    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    public List<Camion> obtenerTodos() {
        return camionRepository.findAll();
    }

    public Optional<Camion> obtenerPorDominio(String dominio) {
        return camionRepository.findById(dominio);
    }

    public Camion guardar(Camion camion) {
        return camionRepository.save(camion);
    }

    public Camion actualizar(Camion camion) {
        Camion existente = camionRepository.findById(camion.getDominio())
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

        // Acá decidís qué campos se pueden modificar y cuáles NO
        existente.setTipoCamion(camion.getTipoCamion());
        existente.setTransportista(camion.getTransportista());
        existente.setDisponible(camion.isDisponible());

        return camionRepository.save(existente);
    }


    public void eliminar(String dominio) {
        camionRepository.deleteById(dominio);
    }

    public Camion cambiarDisponibilidad(String dominio, boolean disponible) {
        Camion camion = camionRepository.findById(dominio)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));
        camion.setDisponible(disponible);
        return camionRepository.save(camion);
    }

    //Metodos nuevos

    //=== Validar peso/volumen que le manden ===
    public boolean puedeTransportar(String dominioCamion, double pesoRequerido, double volumenRequerido) {
        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Camión no encontrado con dominio: " + dominioCamion
                ));

        if (camion.getTipoCamion() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El camión no tiene tipo de camión asignado"
            );
        }

        Double maxPeso = camion.getTipoCamion().getCapacidad_peso();
        Double maxVolumen = camion.getTipoCamion().getCapacidad_volumen();

        if (maxPeso == null || maxVolumen == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El tipo de camión no tiene capacidad configurada"
            );
        }

        return pesoRequerido <= maxPeso && volumenRequerido <= maxVolumen;
    }
}