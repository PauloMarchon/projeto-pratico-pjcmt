package com.paulomarchon.projetopratico.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequisicaoCadastroEndereco(
        @NotBlank @Size(max = 50) String tipoLogradouro,
        @NotBlank @Size(max = 200) String logradouro,
        @NotNull Integer numero,
        @NotBlank @Size(max = 100) String bairro,
        @NotBlank @Size(max = 200) String cidade,
        @NotBlank @Size(min = 2, max = 2) String uf
) {
    public RequisicaoCadastroEndereco {
        tipoLogradouro = tipoLogradouro.toUpperCase();
        logradouro = logradouro.toUpperCase();
        bairro = bairro.toUpperCase();
        cidade = cidade.toUpperCase();
        uf = uf.toUpperCase();
    }
}
