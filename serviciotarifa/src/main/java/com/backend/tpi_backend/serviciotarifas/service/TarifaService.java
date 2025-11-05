package com.backend.tpi_backend.serviciotarifas.service;

import com.backend.tpi_backend.serviciotarifas.model.Tarifa;
import com.backend.tpi_backend.serviciotarifas.repository.TarifaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public List<Tarifa> getAll() {
        return tarifaRepository.findAll();
    }

    public Optional<Tarifa> getById(Long id) {
        return tarifaRepository.findById(id);
    }

    public Tarifa create(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    public Tarifa update(Long id, Tarifa nuevaTarifa) {
        return tarifaRepository.findById(id)
                .map(tarifa -> {
                    tarifa.setCostoFijoTramo(nuevaTarifa.getCostoFijoTramo());
                    tarifa.setIdTipoCamion(nuevaTarifa.getIdTipoCamion());
                    tarifa.setValorLitroCombustible(nuevaTarifa.getValorLitroCombustible());
                    tarifa.setValidoDesde(nuevaTarifa.getValidoDesde());
                    tarifa.setValidoHasta(nuevaTarifa.getValidoHasta());
                    return tarifaRepository.save(tarifa);
                })
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + id));
    }

    public void delete(Long id) {
        tarifaRepository.deleteById(id);
    }

    public List<Tarifa> getTarifasVigentes(LocalDate fecha) {
        return tarifaRepository.findByValidoDesdeBeforeAndValidoHastaAfter(fecha, fecha);
    }
}
