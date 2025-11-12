package com.backend.tpi_backend.serviciodepositos.controller;

import com.backend.tpi_backend.serviciodepositos.model.Deposito;
import com.backend.tpi_backend.serviciodepositos.service.DepositoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depositos")
@Tag(name = "Depósitos", description = "Gestión de depósitos")
public class DepositoController {

    private final DepositoService depositoService;

    // Inyección por constructor (como el esqueleto)
    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @GetMapping
    public List<Deposito> listar() {
        return depositoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable Long id) {
        Deposito deposito = depositoService.buscarPorId(id);
        if (deposito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deposito);
    }

    @PostMapping
    public Deposito crear(@RequestBody Deposito deposito) {
        return depositoService.crear(deposito);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> actualizar(@PathVariable Long id, @RequestBody Deposito deposito) {
        Deposito actualizado = depositoService.actualizar(id, deposito);
        if (actualizado == null) {
            // Esto podría pasar si el ID no existe, aunque 'save' lo crea...
            // Es mejor verificar si existe primero, pero sigamos el patrón de 'ClienteService'
             return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        depositoService.eliminar(id);
        return ResponseEntity.ok().build(); // Devuelve 200 OK
    }
}