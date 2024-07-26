package com.paulomarchon.projetopratico.cidade;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("cidade-jpa")
public class CidadeJpaDataAccessService implements CidadeDao {
    private final CidadeRepository cidadeRepository;

    public CidadeJpaDataAccessService(CidadeRepository cidadeRepository) {
        this.cidadeRepository = cidadeRepository;
    }

    @Override
    public Optional<Cidade> buscarCidade(String nome, UF uf) {
        return cidadeRepository.findByNomeIgnoreCaseAndUf(nome, uf);
    }

    @Override
    public Cidade cadastrarCidade(Cidade cidade) {
        return cidadeRepository.save(cidade);
    }
}
