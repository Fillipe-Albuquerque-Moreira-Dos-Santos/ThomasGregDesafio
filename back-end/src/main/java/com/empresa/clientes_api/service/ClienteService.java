package com.empresa.clientes_api.service;

import com.empresa.clientes_api.dto.ClienteDTO;
import com.empresa.clientes_api.dto.LogradouroDTO;
import com.empresa.clientes_api.exception.ClienteJaCadastradoException;
import com.empresa.clientes_api.exception.ErroProcessandoLogotipoException;
import com.empresa.clientes_api.model.Cliente;
import com.empresa.clientes_api.model.Logradouro;
import com.empresa.clientes_api.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;


    public ClienteDTO criarCliente(String nome, String email, List<String> logradouros, MultipartFile logotipo) {

        // Verifica se o tem email cadastrado
        if (clienteRepository.existsByEmail(email)) {
            throw new ClienteJaCadastradoException();
        }

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);

        // Logotipo ver se tem
        if (logotipo != null && !logotipo.isEmpty()) {
            try {
                cliente.setLogotipo(logotipo.getBytes());
            } catch (IOException e) {
                throw new ErroProcessandoLogotipoException();
            }
        }

        if (logradouros != null && !logradouros.isEmpty()) {
            List<Logradouro> logradourosList = logradouros.stream().map(endereco -> new Logradouro(endereco, cliente)).collect(Collectors.toList());
            cliente.setLogradouros(logradourosList);
        }

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteDTO(clienteSalvo);
    }


    // Método para encontrar todos os clientes
    public List<ClienteDTO> findAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll(); // Obtém todos os clientes do repositório
        return clientes.stream().map(ClienteDTO::new).collect(Collectors.toList());
    }


    public Cliente obterCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public ClienteDTO atualizarCliente(Long id, ClienteDTO clienteDTO, boolean logradouroOnly) throws IOException {
        Cliente cliente = obterCliente(id);

        if (!logradouroOnly) {
            // Se e-mail mudou, verificar duplicidade
            if (!cliente.getEmail().equalsIgnoreCase(clienteDTO.getEmail()) &&
                    clienteRepository.existsByEmail(clienteDTO.getEmail())) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }

            cliente.setNome(clienteDTO.getNome());
            cliente.setEmail(clienteDTO.getEmail());

            if (clienteDTO.getLogotipo() != null && !clienteDTO.getLogotipo().isEmpty()) {
                cliente.setLogotipo(clienteDTO.getLogotipo().getBytes());
            }
        }

        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return new ClienteDTO(clienteAtualizado);
    }

    /**
     * Método específico para atualizar apenas o logradouro de um cliente
     * @param id ID do cliente
     * @param logradouroDTO DTO contendo o novo logradouro
     * @return ClienteDTO com dados atualizados
     */
    public ClienteDTO atualizarLogradouro(Long id, LogradouroDTO logradouroDTO) {
        Cliente cliente = obterCliente(id);

        if (cliente.getLogradouros() == null || cliente.getLogradouros().isEmpty()) {
            cliente.setLogradouros(List.of(new Logradouro(logradouroDTO.getLogradouro(), cliente)));
        } else {
            Logradouro logradouro = cliente.getLogradouros().get(0);
            logradouro.setLogradouro(logradouroDTO.getLogradouro());
        }

        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return new ClienteDTO(clienteAtualizado);
    }

    public boolean deletar(Long id) {
        Cliente cliente = obterCliente(id);
        clienteRepository.delete(cliente);
        return true;
    }

    // Método para upload de logotipo
    public void uploadLogotipo(Long id, MultipartFile logotipo) {
        Cliente cliente = obterCliente(id);

        try {
            // Verificando se o arquivo de logotipo não é nulo
            if (logotipo != null) {
                cliente.setLogotipo(logotipo.getBytes());
                clienteRepository.save(cliente);
            } else {
                throw new RuntimeException("Arquivo de logotipo não pode ser nulo.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o logotipo: " + e.getMessage());
        }
    }
}