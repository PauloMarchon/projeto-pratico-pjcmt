package com.paulomarchon.projetopratico.unidade.dto;

import com.paulomarchon.projetopratico.endereco.dto.EnderecoDto;

public record UnidadeDto(
        Integer id,
        String nome,
        String sigla,
        EnderecoDto endereco
) {
}
