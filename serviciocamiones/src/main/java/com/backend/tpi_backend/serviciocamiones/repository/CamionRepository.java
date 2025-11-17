// serviciocamiones.repository.CamionRepository

package com.backend.tpi_backend.serviciocamiones.repository;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CamionRepository extends JpaRepository<Camion, String> {
}
