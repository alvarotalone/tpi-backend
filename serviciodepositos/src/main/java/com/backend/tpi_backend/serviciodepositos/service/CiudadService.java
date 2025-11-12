package com.backend.tpi_backend.serviciodepositos.service;

import com.backend.tpi_backend.serviciodepositos.model.Ciudad;
import com.backend.tpi_backend.serviciodepositos.repositories.CiudadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadService {

    @Autowired
    private CiudadRepository repo;

    public List<Ciudad> listarTodos() {
        return repo.findAll();
    }

    public Ciudad buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Ciudad crear(Ciudad ciudad) {
        return repo.save(ciudad);
    }

    public Ciudad actualizar(Long id, Ciudad ciudad) {
        ciudad.setId(id);
        return repo.save(ciudad);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}