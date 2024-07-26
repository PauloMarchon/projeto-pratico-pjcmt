package com.paulomarchon.projetopratico.cidade;

import java.util.Optional;

public interface CidadeDao {
    Optional<Cidade> buscarCidade(String nome, UF uf);
    Cidade cadastrarCidade(Cidade cidade);
}
