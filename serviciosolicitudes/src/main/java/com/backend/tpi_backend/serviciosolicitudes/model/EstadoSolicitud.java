package com.backend.tpi_backend.serviciosolicitudes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "estado_solicitud")
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_solicitud")
    private Long id;

    private String descripcion;
}
