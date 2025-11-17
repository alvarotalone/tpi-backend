package com.backend.tpi_backend.serviciosolicitudes.service;


import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudRequestDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudResponseDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.TramoDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.CamionDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.ClienteDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.ContenedorDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.ContenedorUbicacionDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.CoordenadasDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.CostoTotalDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.CostoTramoDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.RutaDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.RutaPosicionDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.RutaTentativaDTO;
import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import com.backend.tpi_backend.serviciosolicitudes.repository.EstadoSolicitudRepository;
import com.backend.tpi_backend.serviciosolicitudes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

@Service
public class SolicitudService {

        private final SolicitudRepository repo;
        private final EstadoSolicitudRepository estadoRepo;
        private final RestTemplate restTemplate;

        @Value("${servicio.tarifas.url}")
        private String tarifasBaseUrl;

        @Value("${servicio.clientes.url}")
        private String clientesBaseUrl;

        @Value("${servicios.rutas.url}")
        private String rutasBaseUrl;

        @Value("${servicio.camiones.url}")
        private String camionesBaseUrl;

        public SolicitudService(SolicitudRepository repo,
                        EstadoSolicitudRepository estadoRepo,
                        RestTemplate restTemplate) {
        this.repo = repo;
        this.estadoRepo = estadoRepo;
        this.restTemplate = restTemplate;
        }

    // 🔹 Listar todas las solicitudes
        public List<Solicitud> findAll() {
        return repo.findAll();
        }

    // 🔹 Buscar solicitud por ID
        public Optional<Solicitud> findById(Long id) {
        return repo.findById(id);
        }

    // 🔹 Guardar una nueva solicitud (versión vieja, usando entidad directa)
        public Solicitud save(Solicitud solicitud) {
        return repo.save(solicitud);
        }

    // 🔹 Eliminar una solicitud por ID
        public void delete(Long id) {
        repo.deleteById(id);
        }

    // 🔹 Actualizar el estado de una solicitud
        public Solicitud updateEstado(Long id, Long idEstado) {
        Optional<Solicitud> optSolicitud = repo.findById(id);
        if (optSolicitud.isEmpty()) return null;

        Solicitud solicitud = optSolicitud.get();

        EstadoSolicitud nuevoEstado = estadoRepo.findById(idEstado).orElse(null);
        if (nuevoEstado == null) return null;

        EstadoSolicitud estadoActual = solicitud.getEstado();
        Long idActual = (estadoActual != null) ? estadoActual.getId() : null;
        Long idNuevo  = nuevoEstado.getId();

        if (!esTransicionValida(idActual, idNuevo)) {
        throw new RuntimeException(
                "Transición de estado no permitida: " + idActual + " -> " + idNuevo
        );
        }

        solicitud.setEstado(nuevoEstado);
        return repo.save(solicitud);
}



        private boolean esTransicionValida(Long actual, Long nuevo) {
        if (actual == null || nuevo == null) return false;

    return (actual == 1 && nuevo == 2)   // BORRADOR -> PROGRAMADA
        || (actual == 2 && nuevo == 3)   // PROGRAMADA -> EN_TRANSITO
        || (actual == 3 && nuevo == 4);  // EN_TRANSITO -> ENTREGADA
}


    // 🔹 Nuevo: crear solicitud usando DTO + integración con servicioclientes
        public SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO dto) {

        // 1) Determinar / crear cliente
        Long idCliente = dto.getIdCliente();

        if (idCliente == null) {
                ClienteDTO nuevoCliente = new ClienteDTO();
                nuevoCliente.setNombre(dto.getNombreCliente());
                nuevoCliente.setApellido(dto.getApellidoCliente());
                nuevoCliente.setTelefono(dto.getTelefonoCliente());
                nuevoCliente.setEmail(dto.getEmailCliente());

                ClienteDTO clienteCreado = restTemplate.postForObject(
                        clientesBaseUrl + "/clientes",
                        nuevoCliente,
                        ClienteDTO.class
                );

                if (clienteCreado == null || clienteCreado.getId() == null) {
                throw new RuntimeException("No se pudo crear el cliente en servicioclientes");
                }
                idCliente = clienteCreado.getId();
        }

        // 2) Determinar / crear contenedor
        Long idContenedor = dto.getIdContenedor();

        if (idContenedor == null) {
                ContenedorDTO nuevoContenedor = new ContenedorDTO();
                nuevoContenedor.setPeso(dto.getPesoContenedor());
                nuevoContenedor.setVolumen(dto.getVolumenContenedor());

                ClienteDTO refCliente = new ClienteDTO();
                refCliente.setId(idCliente);
                nuevoContenedor.setCliente(refCliente);

                ContenedorDTO contenedorCreado = restTemplate.postForObject(
                        clientesBaseUrl + "/contenedores",
                        nuevoContenedor,
                        ContenedorDTO.class
                );

                if (contenedorCreado == null || contenedorCreado.getId() == null) {
                throw new RuntimeException("No se pudo crear el contenedor en servicioclientes");
                }
                idContenedor = contenedorCreado.getId();
        }

        // 3) Estado inicial: BORRADOR
        EstadoSolicitud estadoInicial =
                estadoRepo.findByDescripcion("BORRADOR")
                        .orElseThrow(() -> new RuntimeException("No existe estado BORRADOR"));

        // 4) Crear y guardar Solicitud
        Solicitud s = new Solicitud();
        s.setIdCliente(idCliente);
        s.setIdContenedor(idContenedor);
        s.setLatitudOrigen(dto.getLatitudOrigen());
        s.setLongitudOrigen(dto.getLongitudOrigen());
        s.setLatitudDestino(dto.getLatitudDestino());
        s.setLongitudDestino(dto.getLongitudDestino());

        // La entidad usa BigDecimal e Integer, el DTO usa Double
        if (dto.getCostoEstimado() != null) {
                s.setCostoEstimado(BigDecimal.valueOf(dto.getCostoEstimado()));
        } else {
                s.setCostoEstimado(null);
        }

        if (dto.getTiempoEstimado() != null) {
                s.setTiempoEstimado(dto.getTiempoEstimado().intValue());
        } else {
                s.setTiempoEstimado(null);
        }

        // campos que no estamos usando ahora
        s.setDominioCamion(null);
        s.setIdRuta(null);
        s.setIdTarifa(null);
        s.setCostoFinal(null);
        s.setTiempoReal(null);

        s.setEstado(estadoInicial);

        s = repo.save(s);

        // 5) Mapeo a respuesta
        SolicitudResponseDTO resp = new SolicitudResponseDTO();
        resp.setIdSolicitud(s.getId());                    // PK es 'id'
        resp.setIdCliente(s.getIdCliente());
        resp.setIdContenedor(s.getIdContenedor());
        resp.setLatitudOrigen(s.getLatitudOrigen());
        resp.setLongitudOrigen(s.getLongitudOrigen());
        resp.setLatitudDestino(s.getLatitudDestino());
        resp.setLongitudDestino(s.getLongitudDestino());

        // volver a Double para el DTO
        resp.setCostoEstimado(
                s.getCostoEstimado() != null ? s.getCostoEstimado().doubleValue() : null
        );
        resp.setTiempoEstimado(
                s.getTiempoEstimado() != null ? s.getTiempoEstimado().doubleValue() : null
        );

        resp.setEstadoSolicitud(
                s.getEstado() != null ? s.getEstado().getDescripcion() : null
        );

        return resp;
        }

    // 🔹 Solicitudes pendientes de entrega con filtros opcionales
        public List<Solicitud> buscarPendientes(String estado, Long idCliente, Long idContenedor) {
        List<Solicitud> solicitudes = repo.findAll();

        return solicitudes.stream()
                // solo pendientes (no ENTREGADA)
                .filter(s -> s.getEstado() != null
                        && s.getEstado().getDescripcion() != null
                        && !s.getEstado().getDescripcion().equalsIgnoreCase("ENTREGADA"))

                // filtro por estado (opcional)
                .filter(s -> estado == null
                        || (s.getEstado() != null
                                && s.getEstado().getDescripcion() != null
                                && s.getEstado().getDescripcion().equalsIgnoreCase(estado)))

                // filtro por cliente (opcional)
                .filter(s -> idCliente == null
                        || (s.getIdCliente() != null && s.getIdCliente().equals(idCliente)))

                // filtro por contenedor (opcional)
                .filter(s -> idContenedor == null
                        || (s.getIdContenedor() != null && s.getIdContenedor().equals(idContenedor)))
                .toList();
        }

        public List<RutaTentativaDTO> generarRutasTentativas(Long idSolicitud) {

        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        CoordenadasDTO dto = new CoordenadasDTO();
        dto.setLatO(solicitud.getLatitudOrigen());
        dto.setLonO(solicitud.getLongitudOrigen());
        dto.setLatD(solicitud.getLatitudDestino());
        dto.setLonD(solicitud.getLongitudDestino());

        // Llamada única al endpoint /rutas/tentativas
        RutaTentativaDTO[] rutas = restTemplate.postForObject(
                rutasBaseUrl + "/rutas/tentativas",
                dto,
                RutaTentativaDTO[].class
        );

        return List.of(rutas);
        }


        public SolicitudResponseDTO asignarRuta(Long idSolicitud, Long idRuta) {

        // 1) Buscar la solicitud
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 2) Validar que la ruta exista (pero sin usar este resultado)
        try {
                restTemplate.getForObject(
                        rutasBaseUrl + "/rutas/" + idRuta,
                        Object.class
                );
        } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("La ruta indicada no existe en serviciorutas");
        }

        // 👉👉 **OBTENER LA RUTA**
        RutaDTO ruta = restTemplate.getForObject(
                rutasBaseUrl + "/rutas/" + idRuta,
                RutaDTO.class
        );

        if (ruta == null) {
                throw new RuntimeException("No se pudo obtener la ruta desde serviciorutas");
        }

        // 3) Validar transición de estado
        EstadoSolicitud estadoActual = solicitud.getEstado();
        Long idEstadoActual = (estadoActual != null) ? estadoActual.getId() : null;

        EstadoSolicitud estadoProgramada = estadoRepo.findByDescripcion("PROGRAMADA")
                .orElseThrow(() -> new RuntimeException("No existe estado PROGRAMADA"));

        Long idEstadoProgramada = estadoProgramada.getId();

        if (!esTransicionValida(idEstadoActual, idEstadoProgramada)) {
                throw new RuntimeException(
                        "No se puede asignar ruta: transición de estado no permitida"
                );
        }

        // 4) Asignar datos
        solicitud.setIdRuta(idRuta);
        solicitud.setEstado(estadoProgramada);

        // 👉👉 **Ahora sí podés usar la duración**
        solicitud.setTiempoEstimado(ruta.getDuracionEstimada());

        solicitud = repo.save(solicitud);

        return mapToDTO(solicitud);
        }

        public SolicitudResponseDTO asignarCamion(Long idSolicitud, String dominioCamion) {

        // 1️⃣ Buscar solicitud
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getIdRuta() == null) {
                throw new RuntimeException("La solicitud no tiene una ruta asignada");
        }

        Long idRuta = solicitud.getIdRuta();


        // 2️⃣ Obtener contenedor (peso y volumen)
        ContenedorDTO cont = restTemplate.getForObject(
                clientesBaseUrl + "/contenedores/" + solicitud.getIdContenedor(),
                ContenedorDTO.class
        );

        if (cont == null) {
                throw new RuntimeException("No se pudo obtener el contenedor desde servicioclientes");
        }

        double peso = cont.getPeso();
        double volumen = cont.getVolumen();


        // 3️⃣ Obtener datos de ruta
        RutaDTO ruta = restTemplate.getForObject(
                rutasBaseUrl + "/rutas/" + idRuta,
                RutaDTO.class
        );

        if (ruta == null) {
                throw new RuntimeException("Ruta no encontrada en serviciorutas");
        }


        // 4️⃣ Obtener tramos
        TramoDTO[] tramos = restTemplate.getForObject(
                rutasBaseUrl + "/rutas/" + idRuta + "/tramos",
                TramoDTO[].class
        );

        if (tramos == null || tramos.length == 0) {
                throw new RuntimeException("La ruta no tiene tramos");
        }


        // 4.1️⃣ Calcular fechas aproximadas
        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaFin = fechaInicio.plusSeconds(ruta.getDuracionEstimada());


        // 5️⃣ VALIDAR CAPACIDAD (tu API propia)
        Boolean puedeLlevar = restTemplate.postForObject(
                camionesBaseUrl + "/camiones/" + dominioCamion + "/validar-capacidad",
                Map.of("peso", peso, "volumen", volumen),
                Boolean.class
        );

        if (!Boolean.TRUE.equals(puedeLlevar)) {
                throw new RuntimeException("El camión no puede transportar el contenedor");
        }


        // 6️⃣ Validar disponibilidad
        CamionDTO[] disponibles = restTemplate.getForObject(
                camionesBaseUrl + "/camiones/disponibles?fechaInicio=" + fechaInicio +
                        "&fechaFin=" + fechaFin +
                        "&peso=" + peso +
                        "&volumen=" + volumen,
                CamionDTO[].class
        );

        boolean disponible = Arrays.stream(disponibles)
                .anyMatch(c -> c.getDominio().equals(dominioCamion));

        if (!disponible) {
                throw new RuntimeException("El camión no está disponible en ese rango de fechas");
        }


        // 7️⃣ Reservar camión según TU API
        restTemplate.postForObject(
                camionesBaseUrl + "/camiones/" + dominioCamion + "/disponibilidades",
                Map.of(
                        "fechaInicio", fechaInicio.toString(),
                        "fechaFin", fechaFin.toString()
                ),
                Void.class
        );


        // 8️⃣ Asignar camión a los tramos en MS Rutas
        restTemplate.put(
                rutasBaseUrl + "/rutas/" + idRuta + "/asignar-camion",
                Map.of("dominioCamion", dominioCamion)
        );


        // 9️⃣ Guardar en solicitud
        solicitud.setDominioCamion(dominioCamion);


        // 🔟 Cambiar estado
        EstadoSolicitud estadoProgramada = estadoRepo.findByDescripcion("PROGRAMADA")
                .orElseThrow();

        solicitud.setEstado(estadoProgramada);


        // 1️⃣1️⃣ Guardar
        solicitud = repo.save(solicitud);


        // 1️⃣2️⃣ Devolver DTO
        return mapToDTO(solicitud);
        }


        private SolicitudResponseDTO mapToDTO(Solicitud s) {
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
        }


        //== Buscar contenedores ===
        //== Buscar contenedores ===
        public List<ContenedorUbicacionDTO> obtenerUbicacionesContenedoresEnTransito() {

        // 1) Solicitudes en estado EN_TRANSITO
        List<Solicitud> solicitudesEnTransito =
                repo.findByEstado_Descripcion("EN_TRANSITO");

        if (solicitudesEnTransito.isEmpty()) {
                return Collections.emptyList();
        }

        // 2) Mapear contenedor -> ruta (desde la solicitud)
        Map<Long, Long> contenedorRuta = solicitudesEnTransito.stream()
                .filter(s -> s.getIdContenedor() != null && s.getIdRuta() != null)
                .collect(Collectors.toMap(
                        Solicitud::getIdContenedor,
                        Solicitud::getIdRuta,
                        (existing, replacement) -> existing   // si un contenedor tiene varias solicitudes
                ));

        if (contenedorRuta.isEmpty()) {
                return Collections.emptyList();
        }

        // 3) Pedir a serviciorutas la última posición de cada ruta
        List<Long> rutaIds = contenedorRuta.values().stream()
                .distinct()
                .toList();

        String urlUltimaPosicion = rutasBaseUrl + "/rutas/ultima-posicion";

        RutaPosicionDTO[] rutasArray = restTemplate.postForObject(
                urlUltimaPosicion,
                rutaIds,
                RutaPosicionDTO[].class
        );

        List<RutaPosicionDTO> rutasInfo = (rutasArray != null)
                ? Arrays.asList(rutasArray)
                : Collections.emptyList();

        if (rutasInfo.isEmpty()) {
                return Collections.emptyList();
        }

        Map<Long, RutaPosicionDTO> rutaPosicionMap = rutasInfo.stream()
                .collect(Collectors.toMap(RutaPosicionDTO::getIdRuta, r -> r));

        // 4) Construir DTO final contenedor + ruta + coordenadas
        return contenedorRuta.entrySet().stream()
                .map(e -> {
                        Long idContenedor = e.getKey();
                        Long idRuta = e.getValue();
                        RutaPosicionDTO pos = rutaPosicionMap.get(idRuta);
                        if (pos == null) return null;
                        return new ContenedorUbicacionDTO(
                                idContenedor,
                                idRuta,
                                pos.getLatitudDestino(),
                                pos.getLongitudDestino()
                        );
                })
                .filter(Objects::nonNull)
                .toList();
        }

        // ==========================================================
        // CÁLCULO DEL COSTO ESTIMADO DE UNA SOLICITUD
        // ==========================================================
        public Double calcularCostoEstimado(Long idSolicitud) {

        // 1) Obtener la solicitud
        Solicitud sol = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (sol.getIdRuta() == null) {
                throw new RuntimeException("La solicitud no tiene ruta asignada");
        }

        Long idRuta = sol.getIdRuta();

        // 2) Obtener peso y volumen del contenedor
        ContenedorDTO cont = restTemplate.getForObject(
                clientesBaseUrl + "/contenedores/" + sol.getIdContenedor(),
                ContenedorDTO.class
        );

        if (cont == null) {
                throw new RuntimeException("No se pudo obtener el contenedor");
        }

        double peso = cont.getPeso();
        double volumen = cont.getVolumen();

        // 3) Obtener tramos de la ruta
        TramoDTO[] tramos = restTemplate.getForObject(
                rutasBaseUrl + "/rutas/" + idRuta + "/tramos",
                TramoDTO[].class
        );

        if (tramos == null || tramos.length == 0) {
                throw new RuntimeException("La ruta no contiene tramos");
        }

        // 4) Obtener camiones elegibles
        String urlElegibles =
                camionesBaseUrl + "/camiones/elegibles?peso=" + peso + "&volumen=" + volumen;

        CamionDTO[] elegibles = restTemplate.getForObject(urlElegibles, CamionDTO[].class);

        if (elegibles == null || elegibles.length == 0) {
                throw new RuntimeException("No hay camiones elegibles");
        }

        // 5) Calcular promedios (consumo y costo base)
        double promedioConsumo = Arrays.stream(elegibles)
                .mapToDouble(CamionDTO::getConsumoCombustibleKm)
                .average()
                .orElseThrow(() -> new RuntimeException("No se pudo calcular consumo promedio"));

        double promedioCostoKm = Arrays.stream(elegibles)
                .mapToDouble(CamionDTO::getCostoKm)
                .average()
                .orElseThrow(() -> new RuntimeException("No se pudo calcular costo base promedio"));

        Long tipoPromedio = elegibles[0].getIdTipoCamion();

        // 6) Construir CostoTotalDTO para enviar a servicio-tarifas
        CostoTotalDTO totalDTO = new CostoTotalDTO();
        List<CostoTramoDTO> listaTramosDTO = new ArrayList<>();

        for (TramoDTO t : tramos) {
                CostoTramoDTO dto = new CostoTramoDTO();
                dto.setIdTipoCamion(tipoPromedio);
                dto.setCostoBaseKmCamion(BigDecimal.valueOf(promedioCostoKm));
                dto.setConsumoCombustibleCamion(promedioConsumo);
                dto.setDistanciaEnKm(t.getDistanciaMetros() / 1000.0);
                dto.setFecha(LocalDate.now());
                listaTramosDTO.add(dto);
        }

        totalDTO.setTramos(listaTramosDTO);
        totalDTO.setEstadias(Collections.emptyList()); // por ahora sin estadías

        // 7) Llamar a servicio-tarifas
        Double costoEstimado = restTemplate.postForObject(
                tarifasBaseUrl + "/calcular-total",
                totalDTO,
                Double.class
        );

        // 8) Guardar en solicitud
        sol.setCostoEstimado(BigDecimal.valueOf(costoEstimado));
        repo.save(sol);

        return costoEstimado;
        }

        private Double obtenerPesoContenedor(Long idContenedor) {
        ContenedorDTO cont = restTemplate.getForObject(
                clientesBaseUrl + "/contenedores/" + idContenedor,
                ContenedorDTO.class
        );
        return cont != null ? cont.getPeso() : null;
        }

        private Double obtenerVolumenContenedor(Long idContenedor) {
        ContenedorDTO cont = restTemplate.getForObject(
                clientesBaseUrl + "/contenedores/" + idContenedor,
                ContenedorDTO.class
        );
        return cont != null ? cont.getVolumen() : null;
        }

}
