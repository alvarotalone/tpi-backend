package com.backend.tpi_backend.serviciodepositos.repositories;

import com.backend.tpi_backend.serviciodepositos.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {
}