package com.backend.tpi_backend.serviciodepositos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ubicaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion") // Usamos ID simple, es mejor que un PK compuesto de lat/long
    private Long id;

    private Double latitud;
    private Double longitud;
    
    @Column(name = "direccion_textual") // El DER también la menciona
    private String direccionTextual;

    // FK a Ciudad (Muchos-a-Uno)
    @ManyToOne
    @JoinColumn(name = "id_ciudad") // Coincide con el DER
    private Ciudad ciudad;
}