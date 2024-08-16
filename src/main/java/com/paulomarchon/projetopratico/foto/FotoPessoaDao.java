package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.pessoa.Pessoa;

import java.util.List;

public interface FotoPessoaDao {
    List<FotoPessoa> recuperarTodasFotosDePessoa(Pessoa pessoa);
    void adicionarFotosDePessoa(List<FotoPessoa> fotos);
    void excluirFotoDePessoaPorHash(List<String> hash);
}
