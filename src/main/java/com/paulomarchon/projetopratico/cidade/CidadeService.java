package com.paulomarchon.projetopratico.cidade;

import com.paulomarchon.projetopratico.cidade.dto.RequisicaoCadastroCidade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CidadeService {
    private final CidadeDao cidadeDao;
    private final CadastroCidadeService cadastroCidadeService;

    public CidadeService(@Qualifier("cidade-jpa") CidadeDao cidadeDao, CadastroCidadeService cadastroCidadeService) {
        this.cidadeDao = cidadeDao;
        this.cadastroCidadeService = cadastroCidadeService;
    }

    public Cidade processaRequisicaoDeCidade(RequisicaoCadastroCidade cadastroCidade) {
        UF uf = obtemUFInformado(cadastroCidade.uf());

        return selecionaOuCadastraNovaCidade(cadastroCidade.nome(), uf);
    }

    public Cidade selecionaOuCadastraNovaCidade(String nome, UF uf) {
        return cidadeDao.buscarCidade(nome, uf)
                .orElseGet(() -> cadastroCidadeService.cadastrarNovaCidade(nome, uf));
    }

    public UF obtemUFInformado(String uf) {
        try {
           return UF.valueOf(uf);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("A UF informada nao existe");
        }
    }
}
