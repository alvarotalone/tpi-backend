package com.backend.tpi_backend.serviciocamiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transportista")
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transportista")
    private Long id;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}
