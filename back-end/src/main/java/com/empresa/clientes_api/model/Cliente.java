package com.empresa.clientes_api.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity // Adicionando @Entity para indicar que é uma entidade JPA
@Table(name = "cliente") // Nome da tabela no banco de dados
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gerando o ID automaticamente no banco
    @Column(name = "id_cliente") // Adicionando a coluna com nome correspondente à tabela SQL
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome") // Nome da coluna no banco de dados
    private String nome;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    @Column(name = "email", unique = true) // E-mail único na tabela
    private String email;

    @Lob
    @Column(name = "logotipo") // Coluna para o logotipo no banco
    private byte[] logotipo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Logradouro> logradouros;

}
