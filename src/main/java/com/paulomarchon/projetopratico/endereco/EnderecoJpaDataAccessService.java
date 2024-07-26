package com.paulomarchon.projetopratico.endereco;

import org.springframework.stereotype.Repository;

@Repository("endereco-jpa")
public class EnderecoJpaDataAccessService implements EnderecoDao{
    private final EnderecoRepository enderecoRepository;

    public EnderecoJpaDataAccessService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Override
    public Endereco selecionarEnderecoPorReferenciaDeId(Integer id) {
        return enderecoRepository.getReferenceById(id);
    }

    @Override
    public Endereco salvarEndereco(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    @Override
    public void alterarEndereco(Endereco endereco) {
        enderecoRepository.save(endereco);
    }
}
