package com.paulomarchon.projetopratico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjetoPraticoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(ProjetoPraticoException ex) {
        return ex.problemDetail();
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex) {
        return ex.problemDetail();
    }

    @ExceptionHandler(FalhaNoServicoS3Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleFalhaNoServidorS3Exception(FalhaNoServicoS3Exception ex) {
        return ex.problemDetail();
    }
}
