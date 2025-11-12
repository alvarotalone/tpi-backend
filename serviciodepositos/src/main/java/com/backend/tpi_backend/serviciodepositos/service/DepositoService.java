package com.backend.tpi_backend.serviciodepositos.service;

import com.backend.tpi_backend.serviciodepositos.model.Deposito;
import com.backend.tpi_backend.serviciodepositos.repositories.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositoService {

    @Autowired
    private DepositoRepository repo;

    public List<Deposito> listarTodos() {
        return repo.findAll();
    }

    public Deposito buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Deposito crear(Deposito deposito) {
        return repo.save(deposito);
    }

    public Deposito actualizar(Long id, Deposito deposito) {
        deposito.setId(id);
        return repo.save(deposito);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}