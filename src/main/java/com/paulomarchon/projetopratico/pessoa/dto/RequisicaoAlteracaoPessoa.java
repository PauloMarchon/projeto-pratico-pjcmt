package com.paulomarchon.projetopratico.pessoa.dto;

import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RequisicaoAlteracaoPessoa(
        @Size(max = 200) String nome,
        @Past LocalDate dataNascimento,
        SexoPessoa sexo,
        @Size(max = 200) String nomeMae,
        @Size(max = 200) String nomePai
) {
    public RequisicaoAlteracaoPessoa {
        if (nome != null) nome = nome.toUpperCase();
        if (nomeMae != null) nomeMae = nomeMae.toUpperCase();
        if (nomePai != null) nomePai = nomePai.toUpperCase();
    }
}
