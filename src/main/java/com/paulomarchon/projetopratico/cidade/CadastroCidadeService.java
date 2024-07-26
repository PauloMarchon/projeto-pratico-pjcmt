package com.paulomarchon.projetopratico.cidade;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCidadeService {

    private final CidadeDao cidadeDao;

    public CadastroCidadeService(@Qualifier("cidade-jpa")CidadeDao cidadeDao) {
        this.cidadeDao = cidadeDao;
    }

    @Transactional
    public Cidade cadastrarNovaCidade(String nome, UF uf) {
        Cidade cidade = new Cidade(nome, uf);
        return cidadeDao.cadastrarCidade(cidade);
    }
}
