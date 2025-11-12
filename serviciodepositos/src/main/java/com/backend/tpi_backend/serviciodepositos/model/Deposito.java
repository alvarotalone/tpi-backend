package com.backend.tpi_backend.serviciodepositos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "depositos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deposito") // Coincide con el DER
    private Long id;

    private String nombre;

    @Column(name = "costo_estadia_diario") // Requisito del TPI 
    private Double costoEstadiaDiario;

    // FK a Ubicacion (Muchos-a-Uno)
    // Un depósito está en una ubicación.
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;
}