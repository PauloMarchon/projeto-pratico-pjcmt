package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.cidade.Cidade;
import com.paulomarchon.projetopratico.cidade.UF;
import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.endereco.EnderecoDtoMapper;
import com.paulomarchon.projetopratico.endereco.EnderecoService;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.exception.RecursoNaoEncontradoException;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoAlteracaoUnidade;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoCadastroUnidade;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UnidadeServiceTest {

    @Mock
    private UnidadeDao unidadeDao;
    @Mock
    private EnderecoService enderecoService;
    private UnidadeService emTeste;
    private final EnderecoDtoMapper enderecoDtoMapper = new EnderecoDtoMapper();
    private final UnidadeDtoMapper unidadeDtoMapper = new UnidadeDtoMapper(enderecoDtoMapper);

    private Unidade unidade1;
    private Unidade unidade2;
    private Endereco endereco1;

    @BeforeEach
    void setUp() {
        emTeste = new UnidadeService(unidadeDao, unidadeDtoMapper, enderecoService);

        Cidade cidade = new Cidade("SAO PAULO", UF.SP);

        endereco1 = new Endereco("AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        Endereco endereco2 = new Endereco("RUA", "MARECHAL", 30, "VILA NOVA", cidade);

        unidade1 = new Unidade(1, "UNIDADE PRIMEIRA", "UP", endereco1);
        unidade2 = new Unidade(2, "UNIDADE SEGUNDA", "US", endereco2);
    }

    @Test
    void deveBuscarTodasUnidades(){
        int numeroPagina = 0;
        int quantResultados = 2;
        Pageable paginacao = PageRequest.of(numeroPagina, quantResultados, Sort.by("sigla").and(Sort.by("nome")));

        List<Unidade> unidades = List.of(unidade1, unidade2);
        Page<Unidade> paginaUnidades = new PageImpl<>(unidades, paginacao, quantResultados);

        when(unidadeDao.buscarTodasUnidades(eq(paginacao))).thenReturn(paginaUnidades);

        Page<UnidadeDto> resultadoAtual = emTeste.buscarTodasUnidades(numeroPagina, quantResultados);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getContent()).hasSize(2);

        assertThat(resultadoAtual.getContent().getFirst().nome()).isEqualTo(unidade1.getNome());
        assertThat(resultadoAtual.getContent().getFirst().sigla()).isEqualTo(unidade1.getSigla());
        assertThat(resultadoAtual.getContent().getFirst().endereco().tipoLogradouro()).isEqualTo(endereco1.getTipoLogradouro());
        assertThat(resultadoAtual.getContent().getFirst().endereco().logradouro()).isEqualTo(endereco1.getLogradouro());
        assertThat(resultadoAtual.getContent().getFirst().endereco().numero()).isEqualTo(endereco1.getNumero());
        assertThat(resultadoAtual.getContent().getFirst().endereco().bairro()).isEqualTo(endereco1.getBairro());
        assertThat(resultadoAtual.getContent().getFirst().endereco().cidade()).isEqualTo(endereco1.getCidade().toString());

        assertThat(resultadoAtual.getContent().get(1).nome()).isEqualTo(unidade2.getNome());
        assertThat(resultadoAtual.getContent().get(1).sigla()).isEqualTo(unidade2.getSigla());

        verify(unidadeDao, times(1)).buscarTodasUnidades(paginacao);
    }

    @Test
    void deveSelecionarUnidadePorId(){
        Integer id = 1;

        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.of(unidade1));

        UnidadeDto resultadoEsperado = unidadeDtoMapper.apply(unidade1);

        UnidadeDto resultadoAtual = emTeste.selecionarUnidadePorId(id);

        assertThat(resultadoAtual).isEqualTo(resultadoEsperado);
    }

    @Test
    void deveFalharAoTentarSelecionarUnidadeComIdInexistente(){
        Integer id = 1;

        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emTeste.selecionarUnidadePorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .satisfies(exception -> {
                   RecursoNaoEncontradoException ex = (RecursoNaoEncontradoException) exception;
                   ProblemDetail problemDetail = ex.problemDetail();
                   assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                   assertThat(problemDetail.getTitle()).isEqualTo("Recurso nao encontrado");
                   assertThat(problemDetail.getDetail()).isEqualTo("Unidade com id [%s] nao encontrada".formatted(id));
                   assertThat(problemDetail.getProperties()).containsKey("timestamp");
                });
    }

    @Test
    void deveCadastrarUnidadeCorretamente(){
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade requisicaoUnidade = new RequisicaoCadastroUnidade("UNIDADE PRIMEIRA", "UP", requisicaoEndereco);

        when(enderecoService.salvarEndereco(requisicaoUnidade.endereco())).thenReturn(endereco1);

        emTeste.cadastrarUnidade(requisicaoUnidade);

        ArgumentCaptor<Unidade> unidadeArgumentCaptor = ArgumentCaptor.forClass(Unidade.class);

        verify(unidadeDao).cadastrarUnidade(unidadeArgumentCaptor.capture());

        Unidade unidadeCapturada = unidadeArgumentCaptor.getValue();

        assertThat(unidadeCapturada.getId()).isNull();
        assertThat(unidadeCapturada.getNome()).isEqualTo(requisicaoUnidade.nome());
        assertThat(unidadeCapturada.getSigla()).isEqualTo(requisicaoUnidade.sigla());
        assertThat(unidadeCapturada.getEndereco().getTipoLogradouro()).isEqualTo(requisicaoEndereco.tipoLogradouro());
        assertThat(unidadeCapturada.getEndereco().getLogradouro()).isEqualTo(requisicaoEndereco.logradouro());
        assertThat(unidadeCapturada.getEndereco().getNumero()).isEqualTo(requisicaoEndereco.numero());
        assertThat(unidadeCapturada.getEndereco().getBairro()).isEqualTo(requisicaoEndereco.bairro());
        assertThat(unidadeCapturada.getEndereco().getCidade().getNome()).isEqualTo(requisicaoEndereco.cidade());
        assertThat(unidadeCapturada.getEndereco().getCidade().getUf()).isEqualTo(UF.SP);
    }

    @Test
    void deveRetornarUnidadeCadastrada(){
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade requisicaoUnidade = new RequisicaoCadastroUnidade("UNIDADE PRIMEIRA", "UP", requisicaoEndereco);

        when(enderecoService.salvarEndereco(requisicaoUnidade.endereco())).thenReturn(endereco1);

        UnidadeDto resultadoAtual = emTeste.cadastrarUnidade(requisicaoUnidade);

        assertThat(resultadoAtual.id()).isNull();
        assertThat(resultadoAtual.nome()).isEqualTo(requisicaoUnidade.nome());
        assertThat(resultadoAtual.sigla()).isEqualTo(requisicaoUnidade.sigla());
        assertThat(resultadoAtual.endereco()).isEqualTo(enderecoDtoMapper.apply(endereco1));
    }

    @Test
    void deveAlterarTodasAsPropriedadesDaUnidade(){
        Integer id = 1;
        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.of(unidade1));

        RequisicaoAlteracaoEndereco alteracaoEndereco = new RequisicaoAlteracaoEndereco("VILA", "NOVA ESPERANCA", 99, "OLARIA", null, null);
        RequisicaoAlteracaoUnidade alteracaoUnidade = new RequisicaoAlteracaoUnidade("NOVA UNIDADE PRIMEIRA", "NUP", alteracaoEndereco);

        emTeste.alterarUnidade(id, alteracaoUnidade);

        ArgumentCaptor<Unidade> unidadeArgumentCaptor = ArgumentCaptor.forClass(Unidade.class);

        verify(unidadeDao).alterarUnidade(unidadeArgumentCaptor.capture());
        Unidade unidadeCapturada = unidadeArgumentCaptor.getValue();

        assertThat(unidadeCapturada.getNome()).isEqualTo(alteracaoUnidade.nome());
        assertThat(unidadeCapturada.getSigla()).isEqualTo(alteracaoUnidade.sigla());

        verify(enderecoService, times(1)).alterarEndereco(unidade1.getEndereco().getId(), alteracaoUnidade.endereco());
    }

    @Test
    void deveAlterarSomenteNomeDaUnidade(){
        Integer id = 1;
        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.of(unidade1));

        RequisicaoAlteracaoUnidade alteracaoUnidade = new RequisicaoAlteracaoUnidade("NOVA UNIDADE PRIMEIRA", null, null);

        emTeste.alterarUnidade(id, alteracaoUnidade);

        ArgumentCaptor<Unidade> unidadeArgumentCaptor = ArgumentCaptor.forClass(Unidade.class);

        verify(unidadeDao, times(1)).selecionarUnidadePorId(id);
        verify(unidadeDao).alterarUnidade(unidadeArgumentCaptor.capture());
        Unidade unidadeCapturada = unidadeArgumentCaptor.getValue();

        assertThat(unidadeCapturada.getNome()).isEqualTo(alteracaoUnidade.nome());
        assertThat(unidadeCapturada.getSigla()).isEqualTo(unidade1.getSigla());
        assertThat(unidadeCapturada.getEndereco()).isEqualTo(unidade1.getEndereco());

        verify(enderecoService, never()).salvarEndereco(any());
    }

    @Test
    void deveAlterarSomenteSiglaDaUnidade(){
        Integer id = 1;
        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.of(unidade1));

        RequisicaoAlteracaoUnidade alteracaoUnidade = new RequisicaoAlteracaoUnidade(null, "NUP", null);

        emTeste.alterarUnidade(id, alteracaoUnidade);

        ArgumentCaptor<Unidade> unidadeArgumentCaptor = ArgumentCaptor.forClass(Unidade.class);

        verify(unidadeDao, times(1)).selecionarUnidadePorId(id);
        verify(unidadeDao).alterarUnidade(unidadeArgumentCaptor.capture());
        Unidade unidadeCapturada = unidadeArgumentCaptor.getValue();

        assertThat(unidadeCapturada.getNome()).isEqualTo(unidade1.getNome());
        assertThat(unidadeCapturada.getSigla()).isEqualTo(alteracaoUnidade.sigla());
        assertThat(unidadeCapturada.getEndereco()).isEqualTo(unidade1.getEndereco());

        verify(enderecoService, never()).salvarEndereco(any());
    }

    @Test
    void deveAlterarEnderecoDaUnidade(){
        Integer id = 1;
        RequisicaoAlteracaoEndereco alteracaoEndereco = new RequisicaoAlteracaoEndereco("VILA", "NOVA ESPERANCA", 99, "OLARIA", null, null);
        RequisicaoAlteracaoUnidade alteracaoUnidade = new RequisicaoAlteracaoUnidade(null, null, alteracaoEndereco);

        when(unidadeDao.selecionarUnidadePorId(id)).thenReturn(Optional.of(unidade1));

        emTeste.alterarUnidade(id, alteracaoUnidade);

        verify(unidadeDao, times(1)).selecionarUnidadePorId(id);
        verify(enderecoService, times(1)).alterarEndereco(eq(unidade1.getEndereco().getId()), eq(alteracaoUnidade.endereco()));
        verify(unidadeDao, times(1)).alterarUnidade(unidade1);
    }

    @Test
    void deveExcluirUnidade(){
        Integer id = 1;

        when(unidadeDao.existeUnidade(id)).thenReturn(true);

        emTeste.excluirUnidade(id);

        verify(unidadeDao).excluirUnidade(id);
    }

    @Test
    void deveFalharAoTentarExcluirUnidadeIdInexistente(){
        Integer id = 1;

        when(unidadeDao.existeUnidade(id)).thenReturn(false);

        assertThatThrownBy(() -> emTeste.excluirUnidade(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .satisfies(exception -> {
                    RecursoNaoEncontradoException ex = (RecursoNaoEncontradoException) exception;
                    ProblemDetail problemDetail = ex.problemDetail();
                    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(problemDetail.getTitle()).isEqualTo("Recurso nao encontrado");
                    assertThat(problemDetail.getDetail()).isEqualTo("Unidade com id [%s] nao encontrada".formatted(id));
                    assertThat(problemDetail.getProperties()).containsKey("timestamp");
                });

        verify(unidadeDao, never()).excluirUnidade(id);
    }
}
