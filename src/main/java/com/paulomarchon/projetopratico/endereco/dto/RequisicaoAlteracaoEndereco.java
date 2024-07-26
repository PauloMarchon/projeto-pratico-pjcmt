package com.paulomarchon.projetopratico.endereco.dto;

import jakarta.validation.constraints.Size;

public record RequisicaoAlteracaoEndereco(
       @Size(max = 50) String tipoLogradouro,
       @Size(max = 200) String logradouro,
       Integer numero,
       @Size(max = 100) String bairro,
       String cidade,
       String uf
) {
    public RequisicaoAlteracaoEndereco {
        if (tipoLogradouro != null) tipoLogradouro = tipoLogradouro.toUpperCase();
        if (logradouro != null) logradouro = logradouro.toUpperCase();
        if (bairro != null) bairro = bairro.toUpperCase();
        if (cidade != null) cidade = cidade.toUpperCase();
        if (uf != null) uf = uf.toUpperCase();
    }
}
