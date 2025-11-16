package com.backend.tpi_backend.config; // 

// --- Imports de Spring Security y Configuración ---
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

// --- Imports para el "Traductor" de Roles ---
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// --- Imports de Java Util ---
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        
        http
            .authorizeExchange(exchanges -> exchanges
                
                // --- PERMISOS PÚBLICOS ---
                .pathMatchers("/api/login/oauth2/code/keycloak").permitAll()

                // --- REGLAS ROL: CLIENTE ---
                .pathMatchers(HttpMethod.POST, "/api/solicitudes").hasRole("CLIENTE")
                .pathMatchers(HttpMethod.GET, "/api/solicitudes/{id}/seguimiento").hasRole("CLIENTE")

                // --- REGLAS ROL: TRANSPORTISTA ---
                .pathMatchers(HttpMethod.PUT, "/api/tramos/{id}/iniciar").hasRole("TRANSPORTISTA")
                .pathMatchers(HttpMethod.PUT, "/api/tramos/{id}/finalizar").hasRole("TRANSPORTISTA")

                // --- REGLAS ROL: ADMIN (Operador) ---
                .pathMatchers("/api/tarifas/**").hasRole("ADMIN")
                .pathMatchers("/api/depositos/**").hasRole("ADMIN")
                .pathMatchers("/api/camiones/**").hasRole("ADMIN")
                .pathMatchers("/api/tipos-camion/**").hasRole("ADMIN")
                .pathMatchers("/api/transportistas/**").hasRole("ADMIN")
                .pathMatchers("/api/clientes/**").hasRole("ADMIN")
                .pathMatchers("/api/usuarios/**").hasRole("ADMIN")
                .pathMatchers("/api/rutas/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/solicitudes/{id}/estado").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/api/solicitudes", "/api/solicitudes/**").hasRole("ADMIN")

                // --- REGLAS COMPARTIDAS (Cualquier rol logueado) ---
                .pathMatchers(HttpMethod.GET, "/api/solicitudes/{id}/precio-estimado").authenticated()
                .pathMatchers(HttpMethod.GET, "/api/solicitudes/{id}/precio-final").authenticated()
                .pathMatchers(HttpMethod.GET, "/api/solicitudes/{id}").authenticated()
                .pathMatchers("/api/costos/**").authenticated() 
                .anyExchange().authenticated()
            )
            
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    
    // --- "TRADUCTOR" DE ROLES (Corregido y sin 'var') ---
    
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        
        // 1. Esta es la lógica que lee los roles de Keycloak
        Converter<Jwt, Collection<GrantedAuthority>> keycloakRolesConverter = jwt -> {
            
            Object realmAccessObj = jwt.getClaims().get("realm_access");

            if (realmAccessObj instanceof Map) {
                Map<String, Object> realmAccess = (Map<String, Object>) realmAccessObj;
                
                Object rolesObj = realmAccess.get("roles");

                if (rolesObj instanceof Collection) {
                    Collection<String> roles = (Collection<String>) rolesObj;
                    
                    return roles.stream()
                            .map(roleName -> "ROLE_" + roleName) 
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
            }
            
            return Collections.emptyList();
        };

        // 2. Este es el "puente" que conecta nuestra lógica (1) al mundo reactivo
        ReactiveJwtGrantedAuthoritiesConverterAdapter reactiveAdapter = 
            new ReactiveJwtGrantedAuthoritiesConverterAdapter(keycloakRolesConverter);

        // 3. Este es el convertidor final que usa Spring
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(reactiveAdapter);

        return converter;
    }

    // --- CLASE "PUENTE" (Corregida para devolver Flux) ---
    
    static class ReactiveJwtGrantedAuthoritiesConverterAdapter 
        implements Converter<Jwt, Flux<GrantedAuthority>> {

        private final Converter<Jwt, Collection<GrantedAuthority>> converter;

        public ReactiveJwtGrantedAuthoritiesConverterAdapter(Converter<Jwt, Collection<GrantedAuthority>> converter) {
            this.converter = converter;
        }

        @Override
        public Flux<GrantedAuthority> convert(Jwt jwt) {
            return Flux.fromIterable(converter.convert(jwt)); 
        }
    }
}