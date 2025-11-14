package com.backend.tpi_backend.serviciotarifas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.backend.tpi_backend.serviciotarifas.model.Tarifa;
import com.backend.tpi_backend.serviciotarifas.repository.TarifaRepository;

import com.backend.tpi_backend.serviciotarifas.dto.DepositoDTO; // Importo el DTO nuevo que cree
import com.backend.tpi_backend.serviciotarifas.dto.CostoTramoDTO; 
import com.backend.tpi_backend.serviciotarifas.dto.EstadiaCalculoDTO; 
import com.backend.tpi_backend.serviciotarifas.dto.CostoTotalDTO;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    //RestTemplate dek AppConfig
    @Autowired
    private RestTemplate restTemplate;

    // URL 
    private final String DEPOSITOS_API_URL = "http://localhost:8086/depositos/";

    // metodos CRUD
    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public List<Tarifa> getAll() {
        return tarifaRepository.findAll();
    }

    public Optional<Tarifa> getById(Long id) {
        return tarifaRepository.findById(id);
    }

    public Tarifa create(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    public Tarifa update(Long id, Tarifa nuevaTarifa) {
        return tarifaRepository.findById(id)
                .map(tarifa -> {
                    tarifa.setCostoFijoTramo(nuevaTarifa.getCostoFijoTramo());
                    tarifa.setIdTipoCamion(nuevaTarifa.getIdTipoCamion());
                    tarifa.setValorLitroCombustible(nuevaTarifa.getValorLitroCombustible());
                    tarifa.setValidoDesde(nuevaTarifa.getValidoDesde());
                    tarifa.setValidoHasta(nuevaTarifa.getValidoHasta());
                    return tarifaRepository.save(tarifa);
                })
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + id));
    }

    public void delete(Long id) {
        tarifaRepository.deleteById(id);
    }

    public List<Tarifa> getTarifasVigentes(LocalDate fecha) {
        return tarifaRepository.findByValidoDesdeBeforeAndValidoHastaAfter(fecha, fecha);
    }

    // --- NUEVOS METODOS
    /**
     * Calcular costo de estadía en depósito.
     */
    public Double calcularCostoEstadia(Long idDeposito, LocalDate fechaInicio, LocalDate fechaFin) {

        // 1. tengo que llamar a ServicioDepositos en el Pto 8085
        String url = DEPOSITOS_API_URL + idDeposito;
        DepositoDTO deposito = restTemplate.getForObject(url, DepositoDTO.class);

        if (deposito == null || deposito.getCostoEstadiaDiario() == null) {
            throw new RuntimeException("Depósito no encontrado o sin costo diario: " + idDeposito);
        }

        // Calculo días
        long diasDeEstadia = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (diasDeEstadia == 0) {
            diasDeEstadia = 1; // Mínimo 1 día de estadía
        }

        // Calculo costo total
        return diasDeEstadia * deposito.getCostoEstadiaDiario();
    }

    /*
     * Calcular costo por tramo individual.
     * Busca la tarifa vigente (por fecha y tipo de camión) y calcula el costo total del tramo.
     */
    public Double calcularCostoTramo(CostoTramoDTO dto) { // recibe el DTO que cree. 

        // 1. Encontrar la tarifa vigente para la fecha y tipo de camión
        List<Tarifa> tarifasVigentes = getTarifasVigentes(dto.getFecha());

        Tarifa tarifaAplicable = tarifasVigentes.stream() // filtro de toda la lista. Me quedo con el idTipoCamion que vino del DTO
                .filter(t -> t.getIdTipoCamion().equals(dto.getIdTipoCamion()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró una tarifa vigente para el tipo de camión " 
                                + dto.getIdTipoCamion() + " en la fecha " + dto.getFecha()));

        // 2. Extraer valores (usamos BigDecimal para cálculos monetarios)
        BigDecimal costoFijo = tarifaAplicable.getCostoFijoTramo();
        BigDecimal valorLitro = tarifaAplicable.getValorLitroCombustible();
        
        BigDecimal costoBasePorKm = dto.getCostoBaseKmCamion();
        BigDecimal distancia = BigDecimal.valueOf(dto.getDistanciaEnKm());
        BigDecimal consumoCamion = BigDecimal.valueOf(dto.getConsumoCombustibleCamion());

        // 3. Realizar los cálculos según el TPI
        
        // Costo de combustible = (Valor Litro * Consumo Lts/KM) * Distancia KM
        BigDecimal costoCombustible = valorLitro.multiply(consumoCamion).multiply(distancia);
        
        // Costo por distancia = Costo Base $/KM * Distancia KM
        BigDecimal costoDistancia = costoBasePorKm.multiply(distancia);

        // Costo total = Fijo + Costo Distancia + Costo Combustible
        BigDecimal costoTotal = costoFijo.add(costoDistancia).add(costoCombustible);

        // Devolvemos como Double
        return costoTotal.doubleValue();
    }

    /**
     * Calcular costo total de entrega.
     * Recibe un DTO con la lista de tramos y la lista de estadías, y calcula el costo total sumando los costos individuales.
     */
    public Double calcularCostoTotal(CostoTotalDTO request) {
        
        // 1. Calcular el costo total de todos los tramos
        Double costoTramos = 0.0;
        for (CostoTramoDTO tramoDto : request.getTramos()) {
            // Reutilizamos la lógica que ya creamos mas arriba
            costoTramos += calcularCostoTramo(tramoDto);
        }

        // 2. Calcular el costo total de todas las estadías
        Double costoEstadias = 0.0;
        if (request.getEstadias() != null) { // Puede que un viaje no tenga estadías
            for (EstadiaCalculoDTO estadiaDto : request.getEstadias()) {
                // Nuevamente, reutilizamos la lógica que ya creamos mas arriba
                costoEstadias += calcularCostoEstadia(
                    estadiaDto.getIdDeposito(),
                    estadiaDto.getFechaInicio(),
                    estadiaDto.getFechaFin()
                );
            }
        }

        // 3. Devolver la suma total
        return costoTramos + costoEstadias;
    }
}