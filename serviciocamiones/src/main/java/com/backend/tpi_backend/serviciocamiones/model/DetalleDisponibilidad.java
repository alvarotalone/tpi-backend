package com.backend.tpi_backend.serviciocamiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "detalle_disponibilidad")
public class DetalleDisponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_disponibilidad")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dominio_camion", nullable = false)
    private Camion camion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
}
