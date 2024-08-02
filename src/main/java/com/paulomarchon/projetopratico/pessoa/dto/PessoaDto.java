package com.paulomarchon.projetopratico.pessoa.dto;

public record PessoaDto(
        Integer id,
        String nome,
        String dataNascimento,
        String sexo,
        String nomeMae,
        String nomePai
) {
}
