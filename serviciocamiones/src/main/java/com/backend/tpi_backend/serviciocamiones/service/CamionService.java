package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.dto.CamionDTO;
import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.model.DetalleDisponibilidad;
import com.backend.tpi_backend.serviciocamiones.model.TipoCamion;
import com.backend.tpi_backend.serviciocamiones.repository.CamionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class CamionService {

    private final CamionRepository camionRepository;

    private final DetalleDisponibilidadService detalleDisponibilidadService;


public CamionService(CamionRepository camionRepository,
                    DetalleDisponibilidadService detalleDisponibilidadService) {
    this.camionRepository = camionRepository;
    this.detalleDisponibilidadService = detalleDisponibilidadService;
}


    public List<Camion> obtenerTodos() {
        return camionRepository.findAll();
    }

    public Optional<Camion> obtenerPorDominio(String dominio) {
        return camionRepository.findById(dominio);
    }

    public Camion guardar(Camion camion) {
        return camionRepository.save(camion);
    }

    public Camion actualizar(Camion camion) {
        Camion existente = camionRepository.findById(camion.getDominio())
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

        existente.setTipoCamion(camion.getTipoCamion());
        existente.setTransportista(camion.getTransportista());
        // 👉 ya no existe existente.setDisponible(...)

        return camionRepository.save(existente);
    }

    public void eliminar(String dominio) {
        camionRepository.deleteById(dominio);
    }

    // === Validar peso/volumen que le manden ===
    public boolean puedeTransportar(String dominioCamion, double pesoRequerido, double volumenRequerido) {
        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Camión no encontrado con dominio: " + dominioCamion
                ));

        if (camion.getTipoCamion() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El camión no tiene tipo de camión asignado"
            );
        }

        Double maxPeso = camion.getTipoCamion().getCapacidad_peso();
        Double maxVolumen = camion.getTipoCamion().getCapacidad_volumen();

        if (maxPeso == null || maxVolumen == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El tipo de camión no tiene capacidad configurada"
            );
        }

        return pesoRequerido <= maxPeso && volumenRequerido <= maxVolumen;
    }

    //=== Obtener camiones disponibles ===
    public List<Camion> obtenerCamionesDisponibles(String fechaInicio, String fechaFin) {

    List<Camion> todos = camionRepository.findAll();

    return todos.stream()
            .filter(c -> detalleDisponibilidadService.estaDisponible(c.getDominio(), fechaInicio, fechaFin))
            .toList();
    }

    //=== Reservar un camion (marcarlo ocupado segun la ruta que se le asigno) ===
    public DetalleDisponibilidad reservarCamion(String dominioCamion, String fechaInicio, String fechaFin) {
        return detalleDisponibilidadService.crearBloqueo(dominioCamion, fechaInicio, fechaFin);
    }

    //=== Obtener camiones disponibles y que puedan cargar con el contenedor de la solicitud ===
    public List<Camion> obtenerCamionesDisponibles(
            String fechaInicio,
            String fechaFin,
            double pesoRequerido,
            double volumenRequerido) {

        // 1) Primero filtro por disponibilidad (reuso el método existente)
        List<Camion> disponibles = obtenerCamionesDisponibles(fechaInicio, fechaFin);

        // 2) Sobre esos, filtro por capacidad (reuso puedeTransportar)
        return disponibles.stream()
                .filter(c -> puedeTransportar(
                        c.getDominio(),
                        pesoRequerido,
                        volumenRequerido
                ))
                .toList();
    }

    //****METODO ADICIONAL, no se si dejarlo *****/
    // === Datos técnicos de un camión específico ===
    public Optional<Map<String, Object>> obtenerDatosTecnicos(String dominioCamion) {

        Optional<Camion> optionalCamion = camionRepository.findById(dominioCamion);

        if (optionalCamion.isEmpty()) {
            return Optional.empty();
        }

        Camion camion = optionalCamion.get();

        if (camion.getTipoCamion() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El camión no tiene tipo de camión asignado"
            );
        }

        TipoCamion tipo = camion.getTipoCamion();

        Map<String, Object> datos = Map.of(
                "dominio", camion.getDominio(),
                "id_tipo_camion", tipo.getId(),
                "costo_base_km", tipo.getCosto_base_km(),
                "consumo_combustible", tipo.getConsumo_combustible()
        );

        return Optional.of(datos);
    }

    public CamionDTO toDTO(Camion c) {
        CamionDTO dto = new CamionDTO();

        dto.setDominio(c.getDominio());

        // 🔹 Datos desde TipoCamion (tu entidad real)
        dto.setIdTipoCamion(c.getTipoCamion().getId());
        dto.setCapacidadPeso(c.getTipoCamion().getCapacidad_peso());
        dto.setCapacidadVolumen(c.getTipoCamion().getCapacidad_volumen());
        dto.setCostoBaseKm(c.getTipoCamion().getCosto_base_km());
        dto.setConsumoCombustible(c.getTipoCamion().getConsumo_combustible());


        // 🔹 Transportista
        if (c.getTransportista() != null) {
            dto.setIdTransportista(c.getTransportista().getId());
            dto.setNombreTransportista(
                c.getTransportista().getNombre() + " " + c.getTransportista().getApellido()
            );
        }

        return dto;
    }

    public List<CamionDTO> listarElegibles(Double peso, Double volumen) {

        // 1️⃣ Primero: obtener camiones disponibles en la fecha actual (o rango que quieras)
        String hoy = LocalDate.now().toString();

        List<Camion> disponibles = obtenerCamionesDisponibles(hoy, hoy);

        // 2️⃣ Filtrar por capacidad (esto está en TipoCamion)
        List<Camion> elegibles = disponibles.stream()
                .filter(c -> c.getTipoCamion() != null)
                .filter(c -> c.getTipoCamion().getCapacidad_peso() >= peso)
                .filter(c -> c.getTipoCamion().getCapacidad_volumen() >= volumen)
                .toList();

        // 3️⃣ Mapear a DTO
        return elegibles.stream()
                .map(this::toDTO)
                .toList();
    }




    public CamionDTO buscarPorDominio(String dominio) {
        Camion c = camionRepository.findById(dominio)
            .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + dominio));

        return toDTO(c);
    }

}
