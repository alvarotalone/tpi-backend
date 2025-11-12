package com.backend.tpi_backend.serviciousuarios.controller;

import com.backend.tpi_backend.serviciousuarios.model.Rol;
import com.backend.tpi_backend.serviciousuarios.service.RolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Gestión de roles de usuario")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<Rol> listarRoles() {
        return rolService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRol(@PathVariable Long id) {
        return rolService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.guardar(rol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        rol.setIdRol(id);
        return ResponseEntity.ok(rolService.actualizar(rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
