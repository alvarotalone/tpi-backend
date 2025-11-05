package com.backend.tpi_backend.servicioclientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.tpi_backend.servicioclientes.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Podés agregar consultas personalizadas más adelante, por ejemplo:
    // List<Cliente> findByApellidoContaining(String texto);
}
