package com.paulomarchon.projetopratico.pessoa.dto;

import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RequisicaoCadastroPessoa(
        @NotBlank @Size(max = 200) String nome,
        @NotNull @Past LocalDate dataNascimento,
        @NotNull SexoPessoa sexo,
        @NotBlank @Size(max = 200) String nomeMae,
        @NotBlank @Size(max = 200) String nomePai
) {
    public RequisicaoCadastroPessoa {
        nome = nome.toUpperCase();
        nomeMae = nomeMae.toUpperCase();
        nomePai = nomePai.toUpperCase();
    }
}
