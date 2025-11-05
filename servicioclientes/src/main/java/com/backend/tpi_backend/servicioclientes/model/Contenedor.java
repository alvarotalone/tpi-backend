package com.backend.tpi_backend.servicioclientes.model;

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

    // FK hacia EstadoContenedor
    @ManyToOne
    @JoinColumn(name = "id_estado_contenedor")
    private EstadoContenedor estado;

    // FK a Cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}
