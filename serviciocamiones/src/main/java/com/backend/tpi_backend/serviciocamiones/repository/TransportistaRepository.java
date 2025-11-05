package com.backend.tpi_backend.serviciocamiones.repository;

import com.backend.tpi_backend.serviciocamiones.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    // Long si la PK es id_transportista
}
