package com.empresa.clientes_api.service;

import com.empresa.clientes_api.model.Logradouro;
import com.empresa.clientes_api.repository.LogradouroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class LogradouroService {

    private final LogradouroRepository logradouroRepository;

    public List<Logradouro> listarTodos() {
        return logradouroRepository.findAll();
    }

    public Logradouro salvar(Logradouro logradouro) {
        return logradouroRepository.save(logradouro);
    }

    public Logradouro buscarPorId(Long id) {
        return logradouroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logradouro não encontrado"));
    }

    public void excluir(Long id) {
        logradouroRepository.deleteById(id);
    }
}
