package com.backend.tpi_backend.serviciorutas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiciorutasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiciorutasApplication.class, args);
	}

}
