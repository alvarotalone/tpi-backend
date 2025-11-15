package com.backend.tpi_backend.serviciousuarios.service;

import com.backend.tpi_backend.serviciousuarios.model.Rol;
import com.backend.tpi_backend.serviciousuarios.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> obtenerPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol actualizar(Long id, Rol datos) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + id));

        // solo actualizamos lo que tenga sentido
        existente.setDescripcion(datos.getDescripcion());

        return rolRepository.save(existente);
    }

    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}
