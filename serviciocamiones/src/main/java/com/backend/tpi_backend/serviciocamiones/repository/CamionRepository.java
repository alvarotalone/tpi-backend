package com.backend.tpi_backend.serviciocamiones.repository;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
    // La PK es "dominio", que es String según tu DER
}
