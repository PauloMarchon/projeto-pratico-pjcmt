package com.paulomarchon.projetopratico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

public class RecursoNaoEncontradoException extends ProjetoPraticoException{
    private final String mensagem;

    public RecursoNaoEncontradoException(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public ProblemDetail problemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pb.setTitle("Recurso nao encontrado");
        pb.setDetail(mensagem);
        pb.setProperty("timestamp", Instant.now());

        return pb;
    }
}
