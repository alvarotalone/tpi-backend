package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.Transportista;
import com.backend.tpi_backend.serviciocamiones.repository.TransportistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public TransportistaService(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }

    public Transportista guardar(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public Transportista actualizar(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public void eliminar(Long id) {
        transportistaRepository.deleteById(id);
    }
}
