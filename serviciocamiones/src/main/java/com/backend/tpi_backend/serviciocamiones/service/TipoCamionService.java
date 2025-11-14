package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.TipoCamion;
import com.backend.tpi_backend.serviciocamiones.repository.TipoCamionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class TipoCamionService {

    private final TipoCamionRepository tipoCamionRepository;

    public TipoCamionService(TipoCamionRepository tipoCamionRepository) {
        this.tipoCamionRepository = tipoCamionRepository;
    }

    public List<TipoCamion> obtenerTodos() {
        return tipoCamionRepository.findAll();
    }

    public Optional<TipoCamion> obtenerPorId(Long id) {
        return tipoCamionRepository.findById(id);
    }

    public TipoCamion guardar(TipoCamion tipoCamion) {
        return tipoCamionRepository.save(tipoCamion);
    }

    public TipoCamion actualizar(TipoCamion tipoCamion) {
        return tipoCamionRepository.save(tipoCamion);
    }

    public void eliminar(Long id) {
        tipoCamionRepository.deleteById(id);
    }

    //=== Obtener datos tecnicos ===
        public Optional<Map<String, Object>> obtenerDatosTecnicos(Long id) {
        return tipoCamionRepository.findById(id)
                .map(tipo -> Map.of(
                        "id_tipo_camion", tipo.getId(),
                        "nombre", tipo.getNombre(),
                        "costo_base_km", tipo.getCosto_base_km(),
                        "consumo_combustible", tipo.getConsumo_combustible()
                ));
    }
}
