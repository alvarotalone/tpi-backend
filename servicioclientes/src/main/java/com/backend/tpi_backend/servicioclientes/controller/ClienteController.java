package com.backend.tpi_backend.servicioclientes.controller;

import com.backend.tpi_backend.servicioclientes.model.Cliente;
import com.backend.tpi_backend.servicioclientes.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService servicio;

    @GetMapping
    public List<Cliente> listar() {
        return servicio.listarTodos();
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @PostMapping
    public Cliente crear(@RequestBody Cliente cliente) {
        return servicio.crear(cliente);
    }
    
    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return servicio.actualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
