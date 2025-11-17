package com.backend.tpi_backend.serviciousuarios.service;

import com.backend.tpi_backend.serviciousuarios.exceptions.DatosInvalidosException;
import com.backend.tpi_backend.serviciousuarios.exceptions.ReglaDeNegocioException;
import com.backend.tpi_backend.serviciousuarios.exceptions.RecursoNoEncontradoException;
import com.backend.tpi_backend.serviciousuarios.model.Rol;
import com.backend.tpi_backend.serviciousuarios.model.Usuario;
import com.backend.tpi_backend.serviciousuarios.repository.RolRepository;
import com.backend.tpi_backend.serviciousuarios.repository.UsuarioRepository;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    // --- 2. INYECTAR LA "HERRAMIENTA" DE KEYCLOAK ---
    private final Keycloak keycloakAdminClient;

    // Leemos el realm "tpi-backend" desde application.properties
    @Value("${keycloak.target-realm}")
    private String targetRealm;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          Keycloak keycloakAdminClient) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public List<Usuario> obtenerTodos() {
        log.info("Obteniendo listado completo de usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.debug("Se obtuvieron {} usuarios", usuarios.size());
        return usuarios;
    }

    public Optional<Usuario> obtenerPorNombreUser(String nombreUser) {
        log.info("Buscando usuario por nombreUser: {}", nombreUser);
        Optional<Usuario> usuario = usuarioRepository.findById(nombreUser);
        if (usuario.isEmpty()) {
            log.warn("Usuario no encontrado con nombreUser {}", nombreUser);
        } else {
            log.debug("Usuario encontrado: {}", nombreUser);
        }
        return usuario;
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        log.info("Iniciando creación de usuario {}", usuario.getNombreUser());

        Rol rolReal;
        if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
            Long idRol = usuario.getRol().getIdRol();
            log.debug("Validando rol con ID {}", idRol);
            rolReal = rolRepository.findById(idRol)
                    .orElseThrow(() -> {
                        String msg = "El Rol con ID " + idRol + " no existe.";
                        log.warn(msg);
                        return new ReglaDeNegocioException(msg);
                    });
            usuario.setRol(rolReal);
        } else {
            String msg = "El usuario debe tener un rol asignado.";
            log.warn(msg);
            throw new DatosInvalidosException(msg);
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario {} guardado en BD local", usuarioGuardado.getNombreUser());

        // Crear también en Keycloak
        crearUsuarioEnKeycloak(usuarioGuardado, rolReal.getDescripcion());
        log.info("Usuario {} creado en Keycloak con rol {}", usuarioGuardado.getNombreUser(), rolReal.getDescripcion());

        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizar(String nombreUser, Usuario datos) {
        log.info("Iniciando actualización de usuario {}", nombreUser);

        Usuario existente = usuarioRepository.findById(nombreUser)
                .orElseThrow(() -> {
                    String msg = "Usuario no encontrado con nombreUser " + nombreUser;
                    log.warn(msg);
                    return new RecursoNoEncontradoException(msg);
                });

        log.debug("Actualizando contraseña de usuario {}", nombreUser);
        existente.setContraseña(datos.getContraseña());

        if (datos.getRol() != null && datos.getRol().getIdRol() != null) {
            Long idRol = datos.getRol().getIdRol();
            log.debug("Validando nuevo rol con ID {} para usuario {}", idRol, nombreUser);
            Rol rolReal = rolRepository.findById(idRol)
                    .orElseThrow(() -> {
                        String msg = "El Rol con ID " + idRol + " no existe.";
                        log.warn(msg);
                        return new ReglaDeNegocioException(msg);
                    });
            existente.setRol(rolReal);
        } else {
            String msg = "El usuario debe tener un rol asignado.";
            log.warn(msg);
            throw new DatosInvalidosException(msg);
        }

        Usuario actualizado = usuarioRepository.save(existente);
        log.info("Usuario {} actualizado correctamente en BD local", nombreUser);

        // (Opcional: aquí podrías sincronizar cambios en Keycloak si fuera necesario)

        return actualizado;
    }

    @Transactional
    public void eliminar(String nombreUser) {
        log.info("Iniciando eliminación de usuario {}", nombreUser);

        // 1. Borramos de Keycloak (8090)
        eliminarUsuarioDeKeycloak(nombreUser);

        // 2. Borramos de nuestra BD (MySQL/H2)
        usuarioRepository.deleteById(nombreUser);
        log.info("Usuario {} eliminado de BD local", nombreUser);
    }

    // --- 6. MÉTODOS PRIVADOS DEL "PUENTE" ---

    private void crearUsuarioEnKeycloak(Usuario usuario, String nombreRol) {
        log.debug("Creando usuario {} en Keycloak con rol {}", usuario.getNombreUser(), nombreRol);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(usuario.getContraseña());
        credential.setTemporary(false);

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(usuario.getNombreUser());
        keycloakUser.setEnabled(true);
        keycloakUser.setCredentials(Collections.singletonList(credential));

        RealmResource realmResource = keycloakAdminClient.realm(targetRealm);
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(keycloakUser);
        int status = response.getStatus();
        log.debug("Respuesta de creación en Keycloak para {}: {}", usuario.getNombreUser(), status);

        if (status == 201) {
            String userId = usersResource.searchByUsername(usuario.getNombreUser(), true).get(0).getId();
            RoleRepresentation rolKeycloak = realmResource.roles().get(nombreRol).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(rolKeycloak));
            log.info("Rol {} asignado en Keycloak al usuario {}", nombreRol, usuario.getNombreUser());
        } else {
            String msg = "Error al crear usuario en Keycloak. Status: " + response.getStatusInfo().getReasonPhrase();
            log.error(msg);
            throw new ReglaDeNegocioException(msg);
        }
    }

    private void eliminarUsuarioDeKeycloak(String nombreUser) {
        log.debug("Eliminando usuario {} de Keycloak", nombreUser);

        RealmResource realmResource = keycloakAdminClient.realm(targetRealm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> users = usersResource.searchByUsername(nombreUser, true);

        if (users.isEmpty()) {
            log.warn("El usuario {} no se encontró en Keycloak para ser borrado", nombreUser);
            return;
        }

        String userId = users.get(0).getId();
        usersResource.get(userId).remove();
        log.info("Usuario {} eliminado de Keycloak (id={})", nombreUser, userId);
    }
}
