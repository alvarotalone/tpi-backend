package com.backend.tpi_backend.serviciocamiones.repository;

import com.backend.tpi_backend.serviciocamiones.model.TipoCamion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCamionRepository extends JpaRepository<TipoCamion, Long> {
    // Long si la PK es id_tipo_camion
}
