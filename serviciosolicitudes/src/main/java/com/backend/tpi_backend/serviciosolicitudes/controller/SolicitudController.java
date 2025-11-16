package com.backend.tpi_backend.serviciosolicitudes.controller;

import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import com.backend.tpi_backend.serviciosolicitudes.service.SolicitudService;
import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudRequestDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudResponseDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.CambioEstadoSolicitudDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@Tag(name = "Solicitudes", description = "Gestión de solicitudes de transporte de contenedor")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    // 🔹 Listar todas las solicitudes
    @GetMapping
    public List<Solicitud> listar() {
        return service.findAll();
    }

    // 🔹 Obtener una solicitud por ID
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtener(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear una nueva solicitud (USANDO DTO)
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crear(@RequestBody SolicitudRequestDTO dto) {
        SolicitudResponseDTO resp = service.crearSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 🔹 Actualizar el estado de una solicitud
    @PutMapping("/{id}/estado")
    public ResponseEntity<Solicitud> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Long idEstado) {
        Solicitud actualizado = service.updateEstado(id, idEstado);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    // 🔹 Eliminar una solicitud
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<SolicitudResponseDTO> tracking(@PathVariable Long id) {
        return service.findById(id)
                .map(s -> {
                    SolicitudResponseDTO dto = new SolicitudResponseDTO();
                    dto.setIdSolicitud(s.getId());
                    dto.setIdCliente(s.getIdCliente());
                    dto.setIdContenedor(s.getIdContenedor());
                    dto.setLatitudOrigen(s.getLatitudOrigen());
                    dto.setLongitudOrigen(s.getLongitudOrigen());
                    dto.setLatitudDestino(s.getLatitudDestino());
                    dto.setLongitudDestino(s.getLongitudDestino());

                    dto.setCostoEstimado(
                            s.getCostoEstimado() != null ? s.getCostoEstimado().doubleValue() : null
                    );
                    dto.setTiempoEstimado(
                            s.getTiempoEstimado() != null ? s.getTiempoEstimado().doubleValue() : null
                    );

                    dto.setEstadoSolicitud(
                            s.getEstado() != null ? s.getEstado().getDescripcion() : null
                    );

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
        // 🔹 Consultar solicitudes pendientes de entrega con filtros
    @GetMapping("/pendientes")
    public List<SolicitudResponseDTO> listarPendientes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idContenedor) {

        return service.buscarPendientes(estado, idCliente, idContenedor)
                .stream()
                .map(s -> {
                    SolicitudResponseDTO dto = new SolicitudResponseDTO();
                    dto.setIdSolicitud(s.getId());
                    dto.setIdCliente(s.getIdCliente());
                    dto.setIdContenedor(s.getIdContenedor());
                    dto.setLatitudOrigen(s.getLatitudOrigen());
                    dto.setLongitudOrigen(s.getLongitudOrigen());
                    dto.setLatitudDestino(s.getLatitudDestino());
                    dto.setLongitudDestino(s.getLongitudDestino());

                    dto.setCostoEstimado(
                            s.getCostoEstimado() != null ? s.getCostoEstimado().doubleValue() : null
                    );
                    dto.setTiempoEstimado(
                            s.getTiempoEstimado() != null ? s.getTiempoEstimado().doubleValue() : null
                    );

                    dto.setEstadoSolicitud(
                            s.getEstado() != null ? s.getEstado().getDescripcion() : null
                    );

                    return dto;
                })
                .toList();
    }

    // 🔹 GENERAR 3 RUTAS TENTATIVAS PARA UNA SOLICITUD
    @PostMapping("/{id}/rutas-tentativas")
    public ResponseEntity<?> generarRutasTentativas(@PathVariable Long id) {
        return ResponseEntity.ok(service.generarRutasTentativas(id));
    }

    // 🔹 Asignar una ruta (ya creada en serviciorutas) a la solicitud
    @PutMapping("/{idSolicitud}/asignar-ruta/{idRuta}")
    public ResponseEntity<SolicitudResponseDTO> asignarRuta(
            @PathVariable Long idSolicitud,
            @PathVariable Long idRuta) {

        SolicitudResponseDTO resp = service.asignarRuta(idSolicitud, idRuta);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{idSolicitud}/asignar-camion/{dominio}")
    public ResponseEntity<?> asignarCamion(
            @PathVariable Long idSolicitud,
            @PathVariable String dominio) {

        return ResponseEntity.ok(
                service.asignarCamion(idSolicitud, dominio)
        );
    }


}
