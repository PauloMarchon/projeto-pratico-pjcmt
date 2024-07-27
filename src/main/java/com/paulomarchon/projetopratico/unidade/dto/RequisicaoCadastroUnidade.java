package com.paulomarchon.projetopratico.unidade.dto;

import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequisicaoCadastroUnidade(
        @NotEmpty(message = "Nome e obrigatorio")
        @Size(max = 200)
        String nome,

        @NotEmpty(message = "Sigla e obrigatorio")
        @Size(max = 20)
        String sigla,

        @NotNull
        RequisicaoCadastroEndereco endereco
) {
        public RequisicaoCadastroUnidade {
                nome = nome.toUpperCase();
                sigla = sigla.toUpperCase();
        }
}
