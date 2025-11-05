package com.backend.tpi_backend.serviciocamiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_camion")
public class TipoCamion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_camion")
    private Long id;

    @Column(name = "nombre_tipo_camion")
    private String nombre;

    private Double capacidad_peso;
    private Double capacidad_volumen;
    private Double costo_base_km;
    private Double consumo_combustible;
}
