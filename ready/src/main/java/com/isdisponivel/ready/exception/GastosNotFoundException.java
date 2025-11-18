package com.isdisponivel.ready.exception;

public class GastosNotFoundException extends RuntimeException {

    public GastosNotFoundException(Long id) {
        super("Gasto com ID " + id + " não encontrado");
    }

    public GastosNotFoundException(String mensagem) {
        super(mensagem);
    }

}