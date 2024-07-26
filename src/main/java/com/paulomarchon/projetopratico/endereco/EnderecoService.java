package com.paulomarchon.projetopratico.endereco;

import com.paulomarchon.projetopratico.cidade.Cidade;
import com.paulomarchon.projetopratico.cidade.CidadeService;
import com.paulomarchon.projetopratico.cidade.dto.RequisicaoCadastroCidade;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {
    private final EnderecoDao enderecoDao;
    private final CidadeService cidadeService;

    public EnderecoService(EnderecoDao enderecoDao, CidadeService cidadeService) {
        this.enderecoDao = enderecoDao;
        this.cidadeService = cidadeService;
    }

    public Endereco salvarEndereco(RequisicaoCadastroEndereco cadastroEndereco) {
        Cidade cidade = buscaCidade(cadastroEndereco.cidade(), cadastroEndereco.uf());

        Endereco endereco = new Endereco(
                cadastroEndereco.tipoLogradouro(),
                cadastroEndereco.logradouro(),
                cadastroEndereco.numero(),
                cadastroEndereco.bairro(),
                cidade
        );

        return enderecoDao.salvarEndereco(endereco);
    }

    public void alterarEndereco(Integer enderecoId, RequisicaoAlteracaoEndereco alteracaoEndereco) {
        Endereco endereco = enderecoDao.selecionarEnderecoPorReferenciaDeId(enderecoId);

        boolean alteracaoEfetivada = false;

        if (alteracaoEndereco.tipoLogradouro() != null && !alteracaoEndereco.tipoLogradouro().equals(endereco.getTipoLogradouro())) {
            endereco.setTipoLogradouro(alteracaoEndereco.tipoLogradouro());
            alteracaoEfetivada = true;
        }

        if (alteracaoEndereco.logradouro() != null && !alteracaoEndereco.logradouro().equals(endereco.getLogradouro())) {
            endereco.setLogradouro(alteracaoEndereco.logradouro());
            alteracaoEfetivada = true;
        }

        if (alteracaoEndereco.numero() != null && !alteracaoEndereco.numero().equals(endereco.getNumero())) {
            endereco.setNumero(alteracaoEndereco.numero());
            alteracaoEfetivada = true;
        }

        if (alteracaoEndereco.bairro() != null && !alteracaoEndereco.bairro().equals(endereco.getBairro())) {
            endereco.setBairro(alteracaoEndereco.bairro());
            alteracaoEfetivada = true;
        }

        if (alteracaoEndereco.cidade() != null && alteracaoEndereco.uf() != null) {
            Cidade cidade = buscaCidade(alteracaoEndereco.cidade(), alteracaoEndereco.uf());
            endereco.setCidade(cidade);
            alteracaoEfetivada = true;
        } else if (alteracaoEndereco.cidade() != null) {
            Cidade cidade = buscaCidade(alteracaoEndereco.cidade(), endereco.getCidade().getUf().toString());
            endereco.setCidade(cidade);
            alteracaoEfetivada = true;
        } else if (alteracaoEndereco.uf() != null) {
            Cidade cidade = buscaCidade(endereco.getCidade().getNome(), alteracaoEndereco.uf());
            endereco.setCidade(cidade);
            alteracaoEfetivada = true;
        }

        if (!alteracaoEfetivada) {
            throw new IllegalArgumentException("Erro ao alterar endereco, nenhuma informacao alterada");
        }

        enderecoDao.alterarEndereco(endereco);
    }

    public Cidade buscaCidade(String cidade, String uf) {
        return cidadeService.processaRequisicaoDeCidade(
                new RequisicaoCadastroCidade(
                        cidade,
                        uf
                ));
    }
}
