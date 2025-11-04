package com.backend.tpi_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contenedores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double peso;
    private Double volumen;

    // FK a Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
