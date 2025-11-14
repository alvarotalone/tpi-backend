package com.backend.tpi_backend.serviciocamiones.service;

import com.backend.tpi_backend.serviciocamiones.model.Camion;
import com.backend.tpi_backend.serviciocamiones.model.DetalleDisponibilidad;
import com.backend.tpi_backend.serviciocamiones.repository.CamionRepository;
import com.backend.tpi_backend.serviciocamiones.repository.DetalleDisponibilidadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class DetalleDisponibilidadService {

        private final DetalleDisponibilidadRepository detalleRepo;
        private final CamionRepository camionRepository;

        public DetalleDisponibilidadService(DetalleDisponibilidadRepository detalleRepo,
                                        CamionRepository camionRepository) {
        this.detalleRepo = detalleRepo;
        this.camionRepository = camionRepository;
        }

    // ---------- Utilidades internas ----------

        private LocalDate convertirSoloFecha(String valor) {
        if (valor == null || valor.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La fecha no puede ser nula ni vacía"
                );
        }

        String soloFecha = valor;
        int indiceT = valor.indexOf('T');
        if (indiceT > 0) {
                soloFecha = valor.substring(0, indiceT);
        }

        try {
            return LocalDate.parse(soloFecha); // yyyy-MM-dd
        } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Formato de fecha inválido (se esperaba yyyy-MM-dd)"
                );
        }
        }

        private void validarRango(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La fecha de inicio no puede ser posterior a la de fin"
                );
        }
        }

    // ¿Dos rangos [i1, f1] y [i2, f2] se solapan?
        private boolean seSolapan(LocalDate inicio1, LocalDate fin1,
                        LocalDate inicio2, LocalDate fin2) {
        return !fin1.isBefore(inicio2) && !inicio1.isAfter(fin2);
        // equivalente a: fin1 >= inicio2 && inicio1 <= fin2
        }

    // ---------- Lógica de negocio ----------

    // === Determinar si un camion esta disponible ===
        public boolean estaDisponible(String dominioCamion,
                                String fechaInicioStr,
                                String fechaFinStr) {

        LocalDate inicio = convertirSoloFecha(fechaInicioStr);
        LocalDate fin = convertirSoloFecha(fechaFinStr);
        validarRango(inicio, fin);

        List<DetalleDisponibilidad> detalles =
                detalleRepo.findByCamion_Dominio(dominioCamion);

        boolean haySolapado = detalles.stream()
                .anyMatch(d -> seSolapan(
                        d.getFechaInicio(),
                        d.getFechaFin(),
                        inicio,
                        fin
                ));

        // Si hay algún solapado -> NO está disponible
        return !haySolapado;
        }

    //=== Crear un objeto detalledisponibilidad para el camion al que se le asigno una ruta ===
        public DetalleDisponibilidad crearBloqueo(String dominioCamion,
                                        String fechaInicioStr,
                                        String fechaFinStr) {

        LocalDate inicio = convertirSoloFecha(fechaInicioStr);
        LocalDate fin = convertirSoloFecha(fechaFinStr);
        validarRango(inicio, fin);

        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Camión no encontrado con dominio: " + dominioCamion
                ));

        List<DetalleDisponibilidad> detalles =
                detalleRepo.findByCamion_Dominio(dominioCamion);

        boolean haySolapado = detalles.stream()
                .anyMatch(d -> seSolapan(
                        d.getFechaInicio(),
                        d.getFechaFin(),
                        inicio,
                        fin
                ));

        if (haySolapado) {
                throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El camión ya tiene asignaciones en ese rango de fechas"
                );
        }       

        DetalleDisponibilidad detalle = new DetalleDisponibilidad();
        detalle.setCamion(camion);
        detalle.setFechaInicio(inicio);
        detalle.setFechaFin(fin);

        return detalleRepo.save(detalle);
        }
}
