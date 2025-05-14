package com.empresa.clientes_api.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogradouroDTO {

    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

}