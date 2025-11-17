package com.backend.tpi_backend.serviciousuarios.controller;

import com.backend.tpi_backend.serviciousuarios.exceptions.RecursoNoEncontradoException;
import com.backend.tpi_backend.serviciousuarios.model.Usuario;
import com.backend.tpi_backend.serviciousuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("[GET] /usuarios - Listar usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        log.info("[GET] /usuarios - {} usuarios encontrados", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{nombreUser}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable("nombreUser") String nombreUser) {
        log.info("[GET] /usuarios/{} - Buscar usuario", nombreUser);

        Usuario usuario = usuarioService.obtenerPorNombreUser(nombreUser)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con nombreUser " + nombreUser));

        log.info("[GET] /usuarios/{} - Encontrado", nombreUser);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        log.info("[POST] /usuarios - Crear usuario {}", usuario.getNombreUser());
        Usuario creado = usuarioService.guardar(usuario);
        log.info("[POST] /usuarios - Creado usuario {}", creado.getNombreUser());
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{nombreUser}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable("nombreUser") String nombreUser,
                                                     @RequestBody Usuario usuario) {
        log.info("[PUT] /usuarios/{} - Actualizar usuario", nombreUser);
        Usuario actualizado = usuarioService.actualizar(nombreUser, usuario);
        log.info("[PUT] /usuarios/{} - Actualizado correctamente", nombreUser);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{nombreUser}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("nombreUser") String nombreUser) {
        log.info("[DELETE] /usuarios/{} - Eliminar usuario", nombreUser);
        usuarioService.eliminar(nombreUser);
        log.info("[DELETE] /usuarios/{} - Eliminado correctamente", nombreUser);
        return ResponseEntity.noContent().build();
    }
}
