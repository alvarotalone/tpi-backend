package com.backend.tpi_backend.serviciotarifas.repository;

import com.backend.tpi_backend.serviciotarifas.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    // Buscar tarifas válidas en una fecha determinada
    List<Tarifa> findByValidoDesdeBeforeAndValidoHastaAfter(LocalDate desde, LocalDate hasta);
}
