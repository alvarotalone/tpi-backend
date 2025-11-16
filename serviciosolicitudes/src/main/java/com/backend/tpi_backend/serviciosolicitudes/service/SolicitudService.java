package com.backend.tpi_backend.serviciosolicitudes.service;

import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudRequestDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.SolicitudResponseDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.ClienteDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.ContenedorDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.RutaDTO;
import com.backend.tpi_backend.serviciosolicitudes.dto.RutaTentativaDTO;
import com.backend.tpi_backend.serviciosolicitudes.model.EstadoSolicitud;
import com.backend.tpi_backend.serviciosolicitudes.model.Solicitud;
import com.backend.tpi_backend.serviciosolicitudes.repository.EstadoSolicitudRepository;
import com.backend.tpi_backend.serviciosolicitudes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository repo;
    private final EstadoSolicitudRepository estadoRepo;
    private final RestTemplate restTemplate;

    @Value("${servicio.clientes.url}")
    private String clientesBaseUrl;

    @Value("${servicios.rutas.url}")
    private String rutasBaseUrl;

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

        double latO = solicitud.getLatitudOrigen();
        double lonO = solicitud.getLongitudOrigen();
        double latD = solicitud.getLatitudDestino();
        double lonD = solicitud.getLongitudDestino();

        

        // ### Ruta Directa ###
        var directa = restTemplate.postForObject(
                rutasBaseUrl + "/generar-directa",
                Map.of("latO", latO, "lonO", lonO, "latD", latD, "lonD", lonD),
                RutaTentativaDTO.class
        );

        // ### Ruta Norte ###
        var norte = restTemplate.postForObject(
                rutasBaseUrl + "/generar-norte",
                Map.of("latO", latO, "lonO", lonO, "latD", latD, "lonD", lonD),
                RutaTentativaDTO.class
        );

        // ### Ruta Este ###
        var este = restTemplate.postForObject(
                rutasBaseUrl + "/generar-este",
                Map.of("latO", latO, "lonO", lonO, "latD", latD, "lonD", lonD),
                RutaTentativaDTO.class
        );

        return List.of(directa, norte, este);
    }

    public SolicitudResponseDTO asignarRuta(Long idSolicitud, Long idRuta) {

        // 1) Buscar la solicitud
        Solicitud solicitud = repo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 2) Validar que la ruta exista (pero sin usar este resultado)
        try {
            restTemplate.getForObject(
                    rutasBaseUrl + "/" + idRuta,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("La ruta indicada no existe en serviciorutas");
        }

        // 👉👉 **OBTENER LA RUTA**
        RutaDTO ruta = restTemplate.getForObject(
                rutasBaseUrl + "/" + idRuta,
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
        solicitud.setTiempoEstimado(ruta.getDuracion());

        solicitud = repo.save(solicitud);

        // 5) DTO respuesta
        SolicitudResponseDTO resp = new SolicitudResponseDTO();
        resp.setIdSolicitud(solicitud.getId());
        resp.setIdCliente(solicitud.getIdCliente());
        resp.setIdContenedor(solicitud.getIdContenedor());
        resp.setLatitudOrigen(solicitud.getLatitudOrigen());
        resp.setLongitudOrigen(solicitud.getLongitudOrigen());
        resp.setLatitudDestino(solicitud.getLatitudDestino());
        resp.setLongitudDestino(solicitud.getLongitudDestino());

        resp.setCostoEstimado(
                solicitud.getCostoEstimado() != null ? solicitud.getCostoEstimado().doubleValue() : null
        );

        resp.setTiempoEstimado(
                solicitud.getTiempoEstimado() != null ? solicitud.getTiempoEstimado().doubleValue() : null
        );

        resp.setEstadoSolicitud(
                solicitud.getEstado() != null ? solicitud.getEstado().getDescripcion() : null
        );

        return resp;
    }

}
