package com.paulomarchon.projetopratico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ProjetoPraticoException extends RuntimeException{
    public ProblemDetail problemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pb.setTitle("Erro do servidor interno da aplicacao");
        return pb;
    }
}
