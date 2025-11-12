package com.backend.tpi_backend.serviciousuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.tpi_backend.serviciousuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
