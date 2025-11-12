package com.backend.tpi_backend.serviciodepositos.service;

import com.backend.tpi_backend.serviciodepositos.model.Ubicacion;
import com.backend.tpi_backend.serviciodepositos.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbicacionService {

    @Autowired
    private UbicacionRepository repo;

    public List<Ubicacion> listarTodos() {
        return repo.findAll();
    }

    public Ubicacion buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Ubicacion crear(Ubicacion ubicacion) {
        return repo.save(ubicacion);
    }

    public Ubicacion actualizar(Long id, Ubicacion ubicacion) {
        ubicacion.setId(id);
        return repo.save(ubicacion);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}