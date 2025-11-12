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

    public Rol actualizar(Rol rol) {
        return rolRepository.save(rol);
    }

    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}
