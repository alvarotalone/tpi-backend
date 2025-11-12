package com.backend.tpi_backend.serviciodepositos.repositories;

import com.backend.tpi_backend.serviciodepositos.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    // Métodos de búsqueda personalizados pueden ir aquí si se necesitan
}