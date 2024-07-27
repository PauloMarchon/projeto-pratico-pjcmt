package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.endereco.EnderecoService;
import com.paulomarchon.projetopratico.exception.RecursoNaoEncontradoException;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoAlteracaoUnidade;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoCadastroUnidade;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnidadeService {
    private final UnidadeDao unidadeDao;
    private final EnderecoService enderecoService;
    private final UnidadeDtoMapper unidadeDtoMapper;

    public UnidadeService(@Qualifier("unidade-jpa")UnidadeDao unidadeDao, UnidadeDtoMapper unidadeDtoMapper, EnderecoService enderecoService) {
        this.unidadeDao = unidadeDao;
        this.unidadeDtoMapper = unidadeDtoMapper;
        this.enderecoService = enderecoService;
    }

    public Page<UnidadeDto> buscarTodasUnidades(Integer numeroPagina, Integer quantResultados) {
        Pageable paginacao = PageRequest.of(numeroPagina, quantResultados, Sort.by("sigla").and(Sort.by("nome")));

        Page<Unidade> paginaUnidades = unidadeDao.buscarTodasUnidades(paginacao);

        return paginaUnidades.map(unidadeDtoMapper);
    }

    public UnidadeDto selecionarUnidadePorId(Integer id) {
        return unidadeDao.selecionarUnidadePorId(id)
                .map(unidadeDtoMapper)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade com id [%s] nao encontrada".formatted(id)));
    }

    @Transactional
    public UnidadeDto cadastrarUnidade(RequisicaoCadastroUnidade unidadeRequest) {
        Endereco endereco = enderecoService.salvarEndereco(unidadeRequest.endereco());

        Unidade unidade = new Unidade(
                unidadeRequest.nome(),
                unidadeRequest.sigla(),
                endereco
        );
        unidadeDao.cadastrarUnidade(unidade);

        return unidadeDtoMapper.apply(unidade);
    }

    @Transactional
    public void alterarUnidade(Integer id, RequisicaoAlteracaoUnidade unidadeRequest) {
        Unidade unidade = unidadeDao.selecionarUnidadePorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade com id [%s] nao encontrada".formatted(id)));

        if (unidadeRequest.nome() != null)
            unidade.setNome(unidadeRequest.nome());

        if (unidadeRequest.sigla() != null)
            unidade.setSigla(unidadeRequest.sigla());

        if (unidadeRequest.endereco() != null)
            enderecoService.alterarEndereco(unidade.getEndereco().getId(), unidadeRequest.endereco());

        unidadeDao.alterarUnidade(unidade);
    }

    @Transactional
    public void excluirUnidade(Integer unidadeId) {
        if (!unidadeDao.existeUnidade(unidadeId))
            throw new RecursoNaoEncontradoException("Unidade com id [%s] nao encontrada".formatted(unidadeId));
        unidadeDao.excluirUnidade(unidadeId);
    }
}