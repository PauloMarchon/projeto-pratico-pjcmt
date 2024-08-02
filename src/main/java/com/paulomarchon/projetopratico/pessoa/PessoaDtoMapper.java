package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PessoaDtoMapper implements Function<Pessoa, PessoaDto> {

    @Override
    public PessoaDto apply(Pessoa pessoa) {
        return new PessoaDto(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento().toString(),
                pessoa.getSexo().name(),
                pessoa.getNomeMae(),
                pessoa.getNomePai()
        );
    }
}
