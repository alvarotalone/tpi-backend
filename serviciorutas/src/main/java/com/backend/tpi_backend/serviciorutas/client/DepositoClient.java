package com.backend.tpi_backend.serviciorutas.client;

import com.backend.tpi_backend.serviciorutas.dto.DepositoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(
        name = "serviciodepositos", 
        url = "${servicios.depositos.url}"
)
public interface DepositoClient {

    @GetMapping("/depositos")
    List<DepositoDTO> listarDepositos();
}
