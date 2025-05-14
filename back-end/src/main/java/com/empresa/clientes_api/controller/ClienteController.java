package com.empresa.clientes_api.controller;

import com.empresa.clientes_api.dto.ClienteDTO;
import com.empresa.clientes_api.dto.LogradouroDTO;
import com.empresa.clientes_api.exception.ClienteJaCadastradoException;
import com.empresa.clientes_api.model.Cliente;
import com.empresa.clientes_api.service.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    // Cadastrar cliente com logradouro
    @PostMapping("/salvar-cliente")
    public ResponseEntity<?> criarCliente(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String logradouro,
            @RequestParam(required = false) MultipartFile file) {

        try {
            ClienteDTO resultado = clienteService.criarCliente(nome, email,
                    Collections.singletonList(logradouro), file);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (ClienteJaCadastradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Cliente com email já cadastrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }

    // Listar todos os clientes
    @GetMapping("/listar-todos")
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<ClienteDTO> clientes = clienteService.findAllClientes();
        return ResponseEntity.ok(clientes);
    }

    // Carregar cliente por ID
    @GetMapping("/carregar-clientes/{id}")
    public ResponseEntity<?> carregarCliente(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obterCliente(id);
            return ResponseEntity.ok(new ClienteDTO(cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Cliente não encontrado"));
        }
    }

    // Atualizar cliente
    @PutMapping("/atualizar-cliente/{id}")
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO atualizado = clienteService.atualizarCliente(id, clienteDTO, false);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar cliente: " + e.getMessage()));
        }
    }

    /**
     * Endpoint específico para atualizar apenas o logradouro de um cliente
     */
    @PutMapping("/{id}/atualizar-logradouro")
    public ResponseEntity<?> atualizarLogradouro(
            @PathVariable Long id,
            @RequestBody LogradouroDTO logradouroDTO) {
        try {
            ClienteDTO atualizado = clienteService.atualizarLogradouro(id, logradouroDTO);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar logradouro: " + e.getMessage()));
        }
    }

    // Excluir cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(@PathVariable Long id) {
        try {
            boolean resultado = clienteService.deletar(id);
            if (resultado) {
                return ResponseEntity.ok(Map.of("mensagem", "Cliente excluído com sucesso"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Cliente não encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao excluir cliente: " + e.getMessage()));
        }
    }

    // Upload de logotipo para cliente
    @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadLogotipo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            clienteService.uploadLogotipo(id, file);
            return ResponseEntity.ok(Map.of("mensagem", "Logotipo atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar logotipo: " + e.getMessage()));
        }
    }
}