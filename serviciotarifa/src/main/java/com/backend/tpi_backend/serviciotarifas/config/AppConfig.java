package com.backend.tpi_backend.serviciotarifas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
/* 

CHEQUEAR

Lo que hicimos con AppConfig (La forma "correcta" o "buena")
Al crear ese archivo con @Configuration y @Bean, le estamos diciendo a Spring:

"Oye Spring, quiero que tú seas el dueño de crear y gestionar un objeto RestTemplate. Cuando yo te lo pida, me lo das."
entonces en mi tarifaService o en cualquier servicio solo lo pido. 

*/