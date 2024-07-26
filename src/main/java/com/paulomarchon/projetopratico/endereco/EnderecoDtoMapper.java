package com.paulomarchon.projetopratico.endereco;

import com.paulomarchon.projetopratico.endereco.dto.EnderecoDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class EnderecoDtoMapper implements Function<Endereco, EnderecoDto> {
    @Override
    public EnderecoDto apply(Endereco endereco) {
        return new EnderecoDto(
                endereco.getTipoLogradouro(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade().toString()
        );
    }
}
