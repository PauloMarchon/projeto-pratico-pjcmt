package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.pessoa.Pessoa;

import java.util.List;

public interface FotoPessoaDao {
    List<FotoPessoa> recuperarTodasFotosDePessoa(Pessoa pessoa);
    void adicionarFotoDePessoa(FotoPessoa foto);
    void excluirFotoDePessoaPorHash(List<String> hash);
    boolean existeFotoDePessoaPorHash(String hash);
}
