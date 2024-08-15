package com.paulomarchon.projetopratico.exception;

public class FalhaNoServicoS3Exception extends RuntimeException {
    public FalhaNoServicoS3Exception(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
