package com.paulomarchon.projetopratico.unidade.dto;

import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import jakarta.validation.constraints.Size;

public record RequisicaoAlteracaoUnidade(
        @Size(max = 200) String nome,
        @Size(max = 20) String sigla,
        RequisicaoAlteracaoEndereco endereco
) {
    public RequisicaoAlteracaoUnidade {
        if (nome != null) nome = nome.toUpperCase();
        if (sigla != null) sigla = sigla.toUpperCase();
    }
}
