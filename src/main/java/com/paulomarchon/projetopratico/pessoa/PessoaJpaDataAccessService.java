package com.paulomarchon.projetopratico.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("pessoa-jpa")
public class PessoaJpaDataAccessService implements PessoaDao{
    private final PessoaRepository pessoaRepository;

    public PessoaJpaDataAccessService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public Page<Pessoa> buscarTodasPessoas(Pageable pageable) {
        return pessoaRepository.findAll(pageable);
    }

    @Override
    public Pessoa buscarPessoaPorReferenciaDeId(Integer id) {
        return pessoaRepository.getReferenceById(id);
    }

    @Override
    public Page<Pessoa> buscarPessoaPorNome(String nome, Pageable pageable) {
        return pessoaRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    @Override
    public Pessoa cadastrarPessoa(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    @Override
    public void alterarPessoa(Pessoa pessoa) {
        pessoaRepository.save(pessoa);
    }

    @Override
    public void excluirPessoa(Integer id) {
        pessoaRepository.deleteById(id);
    }

    @Override
    public boolean existePessoa(Integer id) {
        return pessoaRepository.existsById(id);
    }
}
