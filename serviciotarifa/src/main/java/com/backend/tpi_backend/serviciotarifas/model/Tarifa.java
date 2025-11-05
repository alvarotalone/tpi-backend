package com.backend.tpi_backend.serviciotarifas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tarifa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long id;

    @Column(name = "costo_fijo_tramo", nullable = false, precision = 15, scale = 2)
    private BigDecimal costoFijoTramo;

    // Solo guardamos el id del tipo de camión (sin relación JPA)
    @Column(name = "id_tipo_camion", nullable = false)
    private Long idTipoCamion;

    @Column(name = "valor_litro_combustible", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorLitroCombustible;

    @Column(name = "valido_desde", nullable = false)
    private LocalDate validoDesde;

    @Column(name = "valido_hasta", nullable = false)
    private LocalDate validoHasta;

}
