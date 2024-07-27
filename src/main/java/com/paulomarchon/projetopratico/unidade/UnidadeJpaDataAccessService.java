package com.paulomarchon.projetopratico.unidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("unidade-jpa")
public class UnidadeJpaDataAccessService implements UnidadeDao{
    private final UnidadeRepository unidadeRepository;

    public UnidadeJpaDataAccessService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @Override
    public Page<Unidade> buscarTodasUnidades(Pageable pageable) {
        return unidadeRepository.findAll(pageable);
    }

    @Override
    public Optional<Unidade> selecionarUnidadePorId(Integer id) {
        return unidadeRepository.findById(id);
    }

    @Override
    public void cadastrarUnidade(Unidade unidade) {
        unidadeRepository.save(unidade);
    }

    @Override
    public void alterarUnidade(Unidade unidade) {
        unidadeRepository.save(unidade);
    }

    @Override
    public void excluirUnidade(Integer unidadeId) {
        unidadeRepository.deleteById(unidadeId);
    }

    @Override
    public boolean existeUnidade(Integer unidadeId) {
        return unidadeRepository.existsById(unidadeId);
    }
}
