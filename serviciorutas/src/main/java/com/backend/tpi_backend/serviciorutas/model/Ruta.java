package com.backend.tpi_backend.serviciorutas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id;

    @Column(name = "cantidad_tramos", nullable = false)
    private Integer cantidadTramos;

    @Column(name = "cantidad_depositos", nullable = false)
    private Integer cantidadDepositos;
}
