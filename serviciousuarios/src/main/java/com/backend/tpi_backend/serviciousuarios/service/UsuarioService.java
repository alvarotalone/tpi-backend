package com.backend.tpi_backend.serviciousuarios.service;

import com.backend.tpi_backend.serviciousuarios.model.Usuario;
import com.backend.tpi_backend.serviciousuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorNombreUser(String nombreUser) {
        return usuarioRepository.findById(nombreUser);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminar(String nombreUser) {
        usuarioRepository.deleteById(nombreUser);
    }
}
