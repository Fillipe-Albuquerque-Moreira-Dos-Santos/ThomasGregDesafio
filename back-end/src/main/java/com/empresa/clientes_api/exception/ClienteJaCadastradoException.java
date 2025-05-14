package com.empresa.clientes_api.exception;

public class ClienteJaCadastradoException extends RuntimeException {
    public ClienteJaCadastradoException() {
        super("Cliente com o e-mail já cadastrado ");
    }
}
