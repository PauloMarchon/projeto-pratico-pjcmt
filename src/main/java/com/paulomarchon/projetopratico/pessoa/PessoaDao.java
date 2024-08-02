package com.paulomarchon.projetopratico.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaDao {
    Pessoa buscarPessoaPorReferenciaDeId(Integer id);
    Page<Pessoa> buscarTodasPessoas(Pageable pageable);
    Page<Pessoa> buscarPessoaPorNome(String nome, Pageable pageable);
    Pessoa cadastrarPessoa(Pessoa pessoa);
    void alterarPessoa(Pessoa pessoa);
    void excluirPessoa(Integer id);
    boolean existePessoa(Integer id);
}
