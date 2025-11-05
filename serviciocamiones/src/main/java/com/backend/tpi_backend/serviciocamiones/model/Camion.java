package com.backend.tpi_backend.serviciocamiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "camiones")
public class Camion {

    @Id
    @Column(length = 10)
    private String dominio; // PK (ejemplo: AB123CD)

    @ManyToOne
    @JoinColumn(name = "id_tipo_camion")
    private TipoCamion tipoCamion;

    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;

    private boolean disponible;
}
