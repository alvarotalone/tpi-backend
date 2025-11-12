package com.backend.tpi_backend.serviciousuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id
    @Column(name = "nombre_user")
    private String nombreUser;

    private String contraseña;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}
