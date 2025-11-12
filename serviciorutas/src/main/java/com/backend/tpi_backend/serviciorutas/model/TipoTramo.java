package com.backend.tpi_backend.serviciorutas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_tramo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tramo")
    private Long id;

    @Column(nullable = false, length = 100)
    private String descripcion;
}
