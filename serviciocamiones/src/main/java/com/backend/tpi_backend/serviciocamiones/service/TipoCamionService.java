package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.TipoCamion;
import com.backend.tpi_backend.serviciocamiones.repository.TipoCamionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
