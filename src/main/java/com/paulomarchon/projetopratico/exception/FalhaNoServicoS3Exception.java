package com.paulomarchon.projetopratico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

public class FalhaNoServicoS3Exception extends ProjetoPraticoException {
    private final String mensagem;
    private final Throwable causa;

    public FalhaNoServicoS3Exception(String mensagem, Throwable causa) {
        this.mensagem = mensagem;
        this.causa = causa;
    }

    @Override
    public ProblemDetail problemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pb.setTitle("Erro ao realizar operacao com o servidor S3");
        pb.setDetail(mensagem);
        pb.setProperty("causa", causa.getMessage());
        pb.setProperty("timestamp", Instant.now());

        return pb;
    }
}
