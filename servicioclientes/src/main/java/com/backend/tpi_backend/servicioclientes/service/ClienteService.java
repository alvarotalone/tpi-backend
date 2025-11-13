package com.backend.tpi_backend.servicioclientes.service;

import com.backend.tpi_backend.servicioclientes.model.Cliente;
import com.backend.tpi_backend.servicioclientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Cliente crear(Cliente cliente) {
        return repo.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente cliente) {
        cliente.setId(id);
        return repo.save(cliente);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
