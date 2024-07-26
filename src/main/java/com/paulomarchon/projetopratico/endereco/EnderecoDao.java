package com.paulomarchon.projetopratico.endereco;

public interface EnderecoDao {
    Endereco selecionarEnderecoPorReferenciaDeId(Integer id);
    Endereco salvarEndereco(Endereco endereco);
    void alterarEndereco(Endereco endereco);
}
