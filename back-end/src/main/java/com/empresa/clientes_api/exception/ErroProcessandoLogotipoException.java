package com.empresa.clientes_api.exception;

public class ErroProcessandoLogotipoException extends RuntimeException {

    public ErroProcessandoLogotipoException() {
        super("Erro ao processar o logotipo.");
    }
}
