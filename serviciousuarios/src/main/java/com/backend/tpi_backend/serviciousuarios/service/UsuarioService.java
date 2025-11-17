package com.backend.tpi_backend.serviciousuarios.service;

import com.backend.tpi_backend.serviciousuarios.model.Rol;
import com.backend.tpi_backend.serviciousuarios.model.Usuario;
import com.backend.tpi_backend.serviciousuarios.repository.RolRepository;
import com.backend.tpi_backend.serviciousuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// --- 1. IMPORTS NECESARIOS PARA EL "PUENTE" ---
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import jakarta.ws.rs.core.Response;
// --- FIN IMPORTS ---

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                          Keycloak keycloakAdminClient) { // <-- 3. AGREGAR AL CONSTRUCTOR
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.keycloakAdminClient = keycloakAdminClient; // <-- 3. AGREGAR AL CONSTRUCTOR
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorNombreUser(String nombreUser) {
        return usuarioRepository.findById(nombreUser);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        
        // --- 4. LÓGICA DE GUARDADO EN BD (La que ya tenías) ---
        Rol rolReal = null;
        if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
            rolReal = rolRepository.findById(usuario.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Error: El Rol con ID " + usuario.getRol().getIdRol() + " no existe."));
            usuario.setRol(rolReal);
        } else {
            throw new RuntimeException("Error: El usuario debe tener un rol asignado.");
        }
        
        // Guardamos en la BD (MySQL/H2)
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // --- 5. LÓGICA DEL "PUENTE" (Lo nuevo) ---
        // Ahora, creamos el usuario también en Keycloak (8090)
        crearUsuarioEnKeycloak(usuarioGuardado, rolReal.getDescripcion());
        
        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizar(String nombreUser, Usuario datos) {
        // (La lógica de actualizar es más compleja, por ahora la dejamos solo en la BD local)
        Usuario existente = usuarioRepository.findById(nombreUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con nombreUser " + nombreUser));

        existente.setContraseña(datos.getContraseña());

        if (datos.getRol() != null && datos.getRol().getIdRol() != null) {
            Rol rolReal = rolRepository.findById(datos.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Error: El Rol con ID " + datos.getRol().getIdRol() + " no existe."));
            existente.setRol(rolReal);
        } else {
             throw new RuntimeException("Error: El usuario debe tener un rol asignado.");
        }

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void eliminar(String nombreUser) {
        
        // 1. Borramos de Keycloak (8090)
        eliminarUsuarioDeKeycloak(nombreUser);
        
        // 2. Borramos de nuestra BD (MySQL/H2)
        usuarioRepository.deleteById(nombreUser);
    }


    // --- 6. MÉTODOS PRIVADOS DEL "PUENTE" ---

    private void crearUsuarioEnKeycloak(Usuario usuario, String nombreRol) {
        // 1. Definir la contraseña
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(usuario.getContraseña());
        credential.setTemporary(false); // La clave no es temporal

        // 2. Definir el usuario
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(usuario.getNombreUser());
        keycloakUser.setEnabled(true);
        keycloakUser.setCredentials(Collections.singletonList(credential));
        // (Opcional: podés setear email, nombre, etc. si los tenés en tu modelo Usuario)
        // keycloakUser.setEmail(usuario.getEmail()); 
        
        // 3. Obtener el "reino" (tpi-backend)
        RealmResource realmResource = keycloakAdminClient.realm(targetRealm);
        UsersResource usersResource = realmResource.users();

        // 4. Crear el usuario
        Response response = usersResource.create(keycloakUser);

        if (response.getStatus() == 201) {
            // Si se creó (HTTP 201), ahora le asignamos el ROL
            
            // Buscamos el ID del usuario recién creado
            String userId = usersResource.searchByUsername(usuario.getNombreUser(), true).get(0).getId();
            
            // Buscamos el Rol (ej: "cliente")
            RoleRepresentation rolKeycloak = realmResource.roles().get(nombreRol).toRepresentation();
            
            // Asignamos el rol al usuario
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(rolKeycloak));
            
        } else {
            // Si falló, lanzamos un error
            throw new RuntimeException("Error al crear usuario en Keycloak. Status: " + response.getStatusInfo().getReasonPhrase());
        }
    }

    private void eliminarUsuarioDeKeycloak(String nombreUser) {
        // 1. Obtener el "reino" (tpi-backend)
        RealmResource realmResource = keycloakAdminClient.realm(targetRealm);
        UsersResource usersResource = realmResource.users();

        // 2. Buscar al usuario por nombre
        List<UserRepresentation> users = usersResource.searchByUsername(nombreUser, true);
        
        if (users.isEmpty()) {
            // El usuario no existe en Keycloak (quizás se borró a mano), solo logueamos
            System.out.println("WARN: El usuario " + nombreUser + " no se encontró en Keycloak para ser borrado.");
            return;
        }

        // 3. Borrar el usuario
        String userId = users.get(0).getId();
        usersResource.get(userId).remove();
    }
}