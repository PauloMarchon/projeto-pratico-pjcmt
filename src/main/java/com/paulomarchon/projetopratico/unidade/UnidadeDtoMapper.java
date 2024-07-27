package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.endereco.EnderecoDtoMapper;
import com.paulomarchon.projetopratico.endereco.dto.EnderecoDto;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UnidadeDtoMapper implements Function<Unidade, UnidadeDto> {
    private final EnderecoDtoMapper enderecoDtoMapper;

    public UnidadeDtoMapper(EnderecoDtoMapper enderecoDtoMapper) {
        this.enderecoDtoMapper = enderecoDtoMapper;
    }

    @Override
    public UnidadeDto apply(Unidade unidade) {
        EnderecoDto enderecoDto = enderecoDtoMapper.apply(unidade.getEndereco());

        return new UnidadeDto(
                unidade.getId(),
                unidade.getNome(),
                unidade.getSigla(),
                enderecoDto
        );
    }
}
