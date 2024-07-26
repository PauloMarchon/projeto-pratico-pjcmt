package com.paulomarchon.projetopratico.cidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequisicaoCadastroCidade(
        @NotBlank @Size(max = 200) String nome,
        @NotBlank @Size(min = 2, max = 2) String uf
) {
    public RequisicaoCadastroCidade {
        nome = nome.toUpperCase();
        uf = uf.toUpperCase();
    }
}
