package com.backend.tpi_backend.serviciorutas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long id;

    @Column(name = "latitud_origen", nullable = false)
    private Double latitudOrigen;

    @Column(name = "longitud_origen", nullable = false)
    private Double longitudOrigen;

    @Column(name = "latitud_destino", nullable = false)
    private Double latitudDestino;

    @Column(name = "longitud_destino", nullable = false)
    private Double longitudDestino;

    // 🔹 Relación con TipoTramo (misma BD)
    @ManyToOne
    @JoinColumn(name = "id_tipo_tramo", nullable = false)
    private TipoTramo tipoTramo;

    // 🔹 Relación con EstadoTramo (misma BD)
    @ManyToOne
    @JoinColumn(name = "id_estado_tramo", nullable = false)
    private EstadoTramo estadoTramo;

    // 🔹 dominio_camion → pertenece al microservicio camiones (dejar como ID)
    @Column(name = "dominio_camion")
    private String dominioCamion;

    // 🔹 Relación con Ruta (misma BD)
    @ManyToOne
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    @Column(name = "fh_inicio_real")
    private LocalDateTime fhInicioReal;

    @Column(name = "fh_fin_real")
    private LocalDateTime fhFinReal;

    @Column(name = "fh_inicio_estimada")
    private LocalDateTime fhInicioEstimada;

    @Column(name = "fh_fin_estimada")
    private LocalDateTime fhFinEstimada;

    @Column(name = "costo_aproximado", precision = 15, scale = 2)
    private BigDecimal costoAproximado;

    @Column(name = "costo_real", precision = 15, scale = 2)
    private BigDecimal costoReal;
}
