package com.empresa.clientefrontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Cliente {
    private Long id;
    private String nome;
    private String email;
    private List<String> logradouros;
    private String logotipo;


}
