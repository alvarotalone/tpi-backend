package com.backend.tpi_backend.serviciosolicitudes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "solicitud")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {

    // PK de negocio según tu DER
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    // ---- Atributos propios ----
    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado") // p.ej. minutos
    private Integer tiempoEstimado;

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "tiempo_real") // p.ej. minutos
    private Integer tiempoReal;

    // ---- Estado (relación local) ----
    @ManyToOne
    @JoinColumn(name = "id_estado_solicitud")
    private EstadoSolicitud estado;

    // ---- FKs a otros MS (por ahora, solo IDs) ----
    @Column(name = "id_contenedor")
    private Long idContenedor;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "id_tarifa")
    private Long idTarifa;

    // ---- Camión / tracking ----
    @Column(name = "dominio_camion")
    private String dominioCamion;

    @Column(name = "latitud_origen")
    private Double latitudOrigen;

    @Column(name = "longitud_origen")
    private Double longitudOrigen;

    @Column(name = "latitud_destino")
    private Double latitudDestino;

    @Column(name = "longitud_destino")
    private Double longitudDestino;
}
