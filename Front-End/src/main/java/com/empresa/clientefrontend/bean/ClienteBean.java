package com.empresa.clientefrontend.bean;

import com.empresa.clientefrontend.model.Cliente;
import com.empresa.clientefrontend.model.Logradouro;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "clienteBean")
@ViewScoped
@Data
public class ClienteBean implements Serializable {

    private Cliente cliente = new Cliente();
    private boolean logradouroOnly = false;
    private boolean edicaoMode = false;

    private List<Logradouro> todosLogradouros = new ArrayList<>();
    private List<Logradouro> logradourosDisponiveis = new ArrayList<>();
    private List<Logradouro> logradourosSelecionados;


    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();

        carregarLogradouros();
    }

    private void carregarLogradouros() {
        try {
            URL url = new URL("http://localhost:8080/api/logradouros/listar");
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setRequestProperty("Accept", "application/json");

            if (conexao.getResponseCode() != 200) {
                throw new RuntimeException("Erro ao chamar API: HTTP " + conexao.getResponseCode());
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder resposta = new StringBuilder();
                String linha;
                while ((linha = br.readLine()) != null) {
                    resposta.append(linha);
                }

                System.out.println("JSON recebido: " + resposta.toString());

                ObjectMapper mapper = new ObjectMapper();

                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

                todosLogradouros = mapper.readValue(resposta.toString(),
                        new TypeReference<List<Logradouro>>() {});

                System.out.println("Logradouros carregados: " + todosLogradouros.size());
                if (!todosLogradouros.isEmpty()) {
                    System.out.println("Primeiro logradouro: " + todosLogradouros.get(0));
                }

                if (logradourosDisponiveis == null) {
                    logradourosDisponiveis = new ArrayList<>();
                } else {
                    logradourosDisponiveis.clear();
                }

                for (Logradouro log : todosLogradouros) {
                    if (log != null && log.getId() != null && log.getLogradouro() != null &&
                            !log.getLogradouro().trim().isEmpty()) {
                        logradourosDisponiveis.add(log);
                    }
                }

                System.out.println("Logradouros disponíveis após filtragem: " + logradourosDisponiveis.size());

            } finally {
                conexao.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao carregar logradouros", e.getMessage()));
        }
    }

    public String getTituloPagina() {
        if (logradouroOnly) {
            return "Atualização de Logradouro";
        } else if (edicaoMode) {
            return "Edição de Cliente";
        } else {
            return "Cadastro de Cliente";
        }
    }

    public String getTextoBotaoSalvar() {
        if (logradouroOnly) {
            return "Atualizar Logradouro";
        } else if (edicaoMode) {
            return "Atualizar Cliente";
        } else {
            return "Salvar";
        }
    }

    public String cancelar() {
        return "listarClientes?faces-redirect=true";
    }

    public List<Logradouro> getLogradourosSelecionados() {
        return logradourosSelecionados;
    }

    public void setLogradourosSelecionados(List<Logradouro> logradourosSelecionados) {
        this.logradourosSelecionados = logradourosSelecionados;
    }

}