package com.backend.tpi_backend.serviciousuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/**").permitAll() // si todavía tenés algo viejo, no molesta

                        // 🔐 ejemplo de reglas por rol:
                        // GET /usuarios → ADMIN u OPERADOR
                        .requestMatchers(HttpMethod.GET, "/usuarios/**")
                            .hasAnyRole("ADMIN", "OPERADOR")

                        // cualquier operación de escritura sobre usuarios/roles → solo ADMIN
                        .requestMatchers("/usuarios/**", "/roles/**")
                            .hasRole("ADMIN")

                        // todo lo demás, con solo estar logueado alcanza
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        // para que H2 se vea en navegador
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::keycloakRealmRoleConverter);
        return converter;
    }

    private Collection<GrantedAuthority> keycloakRealmRoleConverter(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // leemos realm_access.roles del token de Keycloak
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof Collection<?> roles) {
                for (Object role : roles) {
                    String roleName = role.toString();
                    // Spring espera "ROLE_ADMIN", "ROLE_CLIENTE", etc.
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                }
            }
        }

        return authorities;
    }
}
