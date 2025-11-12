package com.backend.tpi_backend.serviciodepositos.service;

import com.backend.tpi_backend.serviciodepositos.model.Provincia;
import com.backend.tpi_backend.serviciodepositos.repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinciaService {

    @Autowired
    private ProvinciaRepository repo;

    public List<Provincia> listarTodos() {
        return repo.findAll();
    }

    public Provincia buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Provincia crear(Provincia provincia) {
        return repo.save(provincia);
    }

    public Provincia actualizar(Long id, Provincia provincia) {
        provincia.setId(id);
        return repo.save(provincia);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}