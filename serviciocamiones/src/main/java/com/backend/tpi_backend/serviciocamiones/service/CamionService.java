package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.repository.CamionRepository;
import org.springframework.stereotype.Service;

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
        return camionRepository.save(camion);
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

}
