package com.empresa.clientes_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "logradouro")  // Ajustando o nome da tabela para 'logradouro'
@Data
@NoArgsConstructor
public class Logradouro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logradouro") // Coluna correspondente à tabela SQL
    private Long id;

    @Column(name = "logradouro")
    private String logradouro;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    public Logradouro(String logradouro, Cliente cliente) {
        this.logradouro = logradouro;
        this.cliente = cliente;
    }
}
