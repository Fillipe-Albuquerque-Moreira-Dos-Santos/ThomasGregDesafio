package com.empresa.clientes_api.controller;

import com.empresa.clientes_api.model.Logradouro;
import com.empresa.clientes_api.service.LogradouroService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logradouros")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class LogradouroController {

    private final LogradouroService logradouroService;

    @GetMapping("/listar")
    public ResponseEntity<List<Logradouro>> listarTodos() {
        return ResponseEntity.ok(logradouroService.listarTodos());
    }

    @PostMapping("/salvar")
    public ResponseEntity<Logradouro> salvar(@RequestBody Logradouro logradouro) {
        return ResponseEntity.ok(logradouroService.salvar(logradouro));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Logradouro> editar(@PathVariable Long id, @RequestBody Logradouro logradouro) {
        Logradouro existente = logradouroService.buscarPorId(id);
        existente.setLogradouro(logradouro.getLogradouro());
        return ResponseEntity.ok(logradouroService.salvar(existente));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        logradouroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
