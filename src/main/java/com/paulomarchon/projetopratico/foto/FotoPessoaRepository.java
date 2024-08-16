package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.pessoa.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FotoPessoaRepository extends JpaRepository<FotoPessoa, Integer> {
    List<FotoPessoa> findAllByPessoa(Pessoa pessoa);

    @Transactional
    void deleteAllByHashIn(List<String> hash);
}
