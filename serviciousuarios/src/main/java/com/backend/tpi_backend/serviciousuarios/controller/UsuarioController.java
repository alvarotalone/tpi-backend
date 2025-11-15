package com.backend.tpi_backend.serviciousuarios.controller;

import com.backend.tpi_backend.serviciousuarios.model.Usuario;
import com.backend.tpi_backend.serviciousuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{nombreUser}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable String nombreUser) {
        return usuarioService.obtenerPorNombreUser(nombreUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.guardar(usuario);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{nombreUser}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable String nombreUser,
                                                     @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizar(nombreUser, usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{nombreUser}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String nombreUser) {
        usuarioService.eliminar(nombreUser);
        return ResponseEntity.noContent().build();
    }
}
