package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.pessoa.Pessoa;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("foto-pessoa-jpa")
public class FotoPessoaJpaDataAccessService implements  FotoPessoaDao {
    private final FotoPessoaRepository fotoPessoaRepository;

    public FotoPessoaJpaDataAccessService(FotoPessoaRepository fotoPessoaRepository) {
        this.fotoPessoaRepository = fotoPessoaRepository;
    }

    @Override
    public List<FotoPessoa> recuperarTodasFotosDePessoa(Pessoa pessoa) {
        return fotoPessoaRepository.findAllByPessoa(pessoa);
    }

    @Override
    public void adicionarFotosDePessoa(List<FotoPessoa> fotos) {
        fotoPessoaRepository.saveAll(fotos);
    }

    @Override
    public void excluirFotoDePessoaPorHash(List<String> hash) {
        fotoPessoaRepository.deleteAllByHashIn(hash);
    }

    @Override
    public boolean existeFotoDePessoaPorHash(String hash) {
        return fotoPessoaRepository.existsByHash(hash);
    }
}
