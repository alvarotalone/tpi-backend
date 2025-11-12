package com.backend.tpi_backend.serviciodepositos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ciudades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciudad") // Coincide con el DER
    private Long id;

    private String descripcion;

    // FK a Provincia (Muchos-a-Uno)
    @ManyToOne
    @JoinColumn(name = "id_provincia") // Coincide con el DER
    private Provincia provincia;
}