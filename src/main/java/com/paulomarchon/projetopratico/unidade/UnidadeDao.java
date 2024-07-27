package com.paulomarchon.projetopratico.unidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UnidadeDao {
    Page<Unidade> buscarTodasUnidades(Pageable pageable);
    Optional<Unidade> selecionarUnidadePorId(Integer id);
    void cadastrarUnidade(Unidade unidade);
    void alterarUnidade(Unidade unidade);
    void excluirUnidade(Integer unidadeId);
    boolean existeUnidade(Integer unidadeId);
}
