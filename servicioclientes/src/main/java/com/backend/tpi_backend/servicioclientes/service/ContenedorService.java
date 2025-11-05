package com.backend.tpi_backend.servicioclientes.service;

import com.backend.tpi_backend.servicioclientes.model.Contenedor;
import com.backend.tpi_backend.servicioclientes.model.EstadoContenedor;
import com.backend.tpi_backend.servicioclientes.repository.ContenedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;

    public ContenedorService(ContenedorRepository contenedorRepository) {
        this.contenedorRepository = contenedorRepository;
    }

    public List<Contenedor> findAll() {
        return contenedorRepository.findAll();
    }

    public Optional<Contenedor> findById(Long id) {
        return contenedorRepository.findById(id);
    }

    public Contenedor save(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    public void delete(Long id) {
        contenedorRepository.deleteById(id);
    }
    
    public Contenedor updateEstado(Long id, EstadoContenedor nuevoEstado) {
        Optional<Contenedor> cont = contenedorRepository.findById(id);
        if (cont.isPresent()) {
            Contenedor c = cont.get();
            c.setEstado(nuevoEstado);
            return contenedorRepository.save(c);
        }
        return null;
    }
}
