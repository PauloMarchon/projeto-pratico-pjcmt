package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.endereco.EnderecoService;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.exception.RecursoNaoEncontradoException;
import com.paulomarchon.projetopratico.foto.FotoPessoaService;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoAlteracaoPessoa;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PessoaServiceTest {

    @Mock
    private PessoaDao pessoaDao;
    @Mock
    private EnderecoService enderecoService;
    @Mock
    private FotoPessoaService fotoPessoaService;
    private PessoaService emTeste;
    private final PessoaDtoMapper pessoaDtoMapper = new PessoaDtoMapper();

    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private Pessoa pessoa3;

    @BeforeEach
    void setUp() {
        emTeste = new PessoaService(pessoaDao, pessoaDtoMapper, fotoPessoaService ,enderecoService);

        Endereco endereco1 = new Endereco(1,"RUA", "SAO JOSE", 20, "VILA NOVA", null);

        pessoa1 = new Pessoa(1, "MARCELO FERNANDES", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoa1.setEndereco(endereco1);
        pessoa2 = new Pessoa(2, "MARCELO FERNANDES", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoa3 = new Pessoa(3, "AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
    }

    @Test
    @DisplayName("buscarTodasPessoas: Deve retornar todas as pessoas de forma paginada")
    void buscarTodasPessoas_quandoChamado_entaoRetornaTodasPessoasPaginadas() {
        int pagina = 0;
        int tamanho = 10;
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome"));

        List<Pessoa> pessoas = List.of(pessoa1, pessoa2, pessoa3);
        Page<Pessoa> paginaPessoas = new PageImpl<>(pessoas, pageable, tamanho);

        when(pessoaDao.buscarTodasPessoas(eq(pageable))).thenReturn(paginaPessoas);

        Page<PessoaDto> resultadoAtual = emTeste.buscarTodasPessoas(pagina, tamanho);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getContent()).hasSize(3);
        assertThat(resultadoAtual.getContent().getFirst().nome()).isEqualTo(pessoa1.getNome());
        assertThat(resultadoAtual.getContent().get(1).nome()).isEqualTo(pessoa2.getNome());
        assertThat(resultadoAtual.getContent().get(2).nome()).isEqualTo(pessoa3.getNome());

        verify(pessoaDao, times(1)).buscarTodasPessoas(pageable);
    }

    @Test
    @DisplayName("buscarPessoaPorNome: Deve retornar as pessoas com nome correspondente de forma paginada")
    void buscarPessoaPorNome_quandoNomeCorresponder_entaoRetornaPessoasPaginadas() {
        int pagina = 0;
        int tamanho = 10;
        String nome = "MARCELO";
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome"));

        List<Pessoa> pessoas = List.of(pessoa1, pessoa2);
        Page<Pessoa> paginaPessoas = new PageImpl<>(pessoas, pageable, tamanho);

        when(pessoaDao.buscarPessoaPorNome(eq(nome), eq(pageable))).thenReturn(paginaPessoas);

        Page<PessoaDto> resultadoAtual = emTeste.buscarPessoaPorNome(nome, pagina, tamanho);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getContent()).hasSize(2);
        assertThat(resultadoAtual.getTotalElements()).isEqualTo(10);
        assertThat(resultadoAtual.getContent().getFirst().nome()).isEqualTo(pessoa1.getNome());
        assertThat(resultadoAtual.getContent().get(1).nome()).isEqualTo(pessoa2.getNome());
    }

    @Test
    @DisplayName("buscarPessoaPorNome: Deve retornar pagina vazia quando nenhum nome corresponder")
    void buscarPessoaPorNome_quandoNomeNaoCorresponder_entaoRetornaPaginaVazia() {
        int pagina = 0;
        int tamanho = 10;
        String nome = "LUDMILLA";
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome"));

        List<Pessoa> pessoas = List.of();
        Page<Pessoa> paginaPessoas = new PageImpl<>(pessoas, pageable, tamanho);

        when(pessoaDao.buscarPessoaPorNome(eq(nome), eq(pageable))).thenReturn(paginaPessoas);

        Page<PessoaDto> resultadoAtual = emTeste.buscarPessoaPorNome(nome, pagina, tamanho);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getContent()).hasSize(0);
        assertThat(resultadoAtual.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve efetuar o cadastro da pessoa com sucesso")
    void cadastrarPessoa_quandoChamado_entaoCadastraPessoaComSucesso() {
        LocalDate dataNascimento = LocalDate.now();
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("MARCELO FERNANDES", dataNascimento, SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        when(pessoaDao.cadastrarPessoa(any(Pessoa.class))).thenReturn(pessoa1);

        emTeste.cadastrarPessoa(cadastroPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);
        verify(pessoaDao, times(1)).cadastrarPessoa(pessoaArgumentCaptor.capture());

        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada).isNotNull();
        assertThat(pessoaCapturada.getNome()).isEqualTo(cadastroPessoa.nome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(dataNascimento);
        assertThat(pessoaCapturada.getSexo()).isEqualTo(cadastroPessoa.sexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(cadastroPessoa.nomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(cadastroPessoa.nomePai());
    }

    @Test
    @DisplayName("Deve retornar a pessoa cadastrada com sucesso como PessoaDto")
    void cadastrarPessoa_quandoSucesso_entaoRetornaPessoaDtoCadastrada() {
        LocalDate dataNascimento = LocalDate.now();
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("MARCELO FERNANDES", dataNascimento, SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        Pessoa pessoa = new Pessoa(
                cadastroPessoa.nome(),
                cadastroPessoa.dataNascimento(),
                cadastroPessoa.sexo(),
                cadastroPessoa.nomeMae(),
                cadastroPessoa.nomePai()
        );
        when(pessoaDao.cadastrarPessoa(any(Pessoa.class))).thenReturn(pessoa);

        PessoaDto resultadoEsperado = pessoaDtoMapper.apply(pessoa);

        PessoaDto resultadoAtual = emTeste.cadastrarPessoa(cadastroPessoa);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.id()).isEqualTo(resultadoEsperado.id());
        assertThat(resultadoAtual.nome()).isEqualTo(resultadoEsperado.nome());
        assertThat(resultadoAtual.dataNascimento()).isEqualTo(resultadoEsperado.dataNascimento());
        assertThat(resultadoAtual.sexo()).isEqualTo(resultadoEsperado.sexo());
        assertThat(resultadoAtual.nomeMae()).isEqualTo(resultadoEsperado.nomeMae());
        assertThat(resultadoAtual.nomePai()).isEqualTo(resultadoEsperado.nomePai());

        verify(pessoaDao, times(1)).cadastrarPessoa(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve alterar todas as propriedades de pessoa e salvar as alteracoes com sucesso")
    void alterarPessoa_quandoAlterarTodasPropriedades_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        LocalDate dataNascimento = LocalDate.now();
        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa("ROBERTA SOUZA", dataNascimento, SexoPessoa.FEMININO, "LETICIA", "MARCOS");

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(alteracaoPessoa.nome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(alteracaoPessoa.dataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(alteracaoPessoa.sexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(alteracaoPessoa.nomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(alteracaoPessoa.nomePai());
    }

    @Test
    @DisplayName("Deve alterar apenas o nome da pessoa e salvar a alteracao com sucesso")
    void alterarPessoa_quandoAlterarNome_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa("RONALDO", null, null, null, null);

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(alteracaoPessoa.nome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(pessoa1.getDataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(pessoa1.getSexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(pessoa1.getNomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(pessoa1.getNomePai());
    }

    @Test
    @DisplayName("Deve alterar apenas data de nascimento de pessoa e salvar a alteracao com sucesso")
    void alterarPessoa_quandoAlterarDataDeNascimento_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        LocalDate dataNascimento = LocalDate.of(1990, 10, 12);
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa(null, dataNascimento, null, null, null);

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(pessoa1.getNome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(alteracaoPessoa.dataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(pessoa1.getSexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(pessoa1.getNomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(pessoa1.getNomePai());
    }

    @Test
    @DisplayName("Deve alterar apenas o sexo da pessoa e salvar a alteracao com sucesso")
    void alterarPessoa_quandoAlterarSexo_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa(null, null, SexoPessoa.FEMININO, null, null);

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(pessoa1.getNome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(pessoa1.getDataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(alteracaoPessoa.sexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(pessoa1.getNomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(pessoa1.getNomePai());
    }

    @Test
    @DisplayName("Deve alterar apenas o nome da mae da pessoa e salvar a alteracao com sucesso")
    void alterarPessoa_quandoAlterarNomeMae_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa(null, null, null, "MARIA", null);

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(pessoa1.getNome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(pessoa1.getDataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(pessoa1.getSexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(alteracaoPessoa.nomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(pessoa1.getNomePai());
    }

    @Test
    @DisplayName("Deve alterar apenas o nome do pai da pessoa e salva a alteracao com sucesso")
    void alterarPessoa_quandoAlterarNomePai_entaoSalvaAlteracaoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoPessoa alteracaoPessoa = new RequisicaoAlteracaoPessoa(null, null, null, null, "JOSE");

        emTeste.alterarPessoa(id, alteracaoPessoa);

        ArgumentCaptor<Pessoa> pessoaArgumentCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaDao).alterarPessoa(pessoaArgumentCaptor.capture());
        Pessoa pessoaCapturada = pessoaArgumentCaptor.getValue();

        assertThat(pessoaCapturada.getNome()).isEqualTo(pessoa1.getNome());
        assertThat(pessoaCapturada.getDataNascimento()).isEqualTo(pessoa1.getDataNascimento());
        assertThat(pessoaCapturada.getSexo()).isEqualTo(pessoa1.getSexo());
        assertThat(pessoaCapturada.getNomeMae()).isEqualTo(pessoa1.getNomeMae());
        assertThat(pessoaCapturada.getNomePai()).isEqualTo(alteracaoPessoa.nomePai());
    }

    @Test
    @DisplayName("Deve cadastrar o endereco de Pessoa com sucesso quando pessoa existir")
    void cadastrarEnderecoDePessoa_quandoPessoaExistir_entaoCadastraEnderecoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoCadastroEndereco cadastroEndereco = new RequisicaoCadastroEndereco("RUA", "SAO JOSE", 20, "VILA NOVA", "SAO PAULO", "SP");

        emTeste.cadastrarEnderecoDePessoa(id, cadastroEndereco);

        ArgumentCaptor<RequisicaoCadastroEndereco> cadastroEnderecoArgumentCaptor = ArgumentCaptor.forClass(RequisicaoCadastroEndereco.class);
        verify(enderecoService, times(1)).salvarEndereco(cadastroEnderecoArgumentCaptor.capture());

        RequisicaoCadastroEndereco cadastroEnderecoCapturado = cadastroEnderecoArgumentCaptor.getValue();

        assertThat(cadastroEnderecoCapturado.tipoLogradouro()).isEqualTo(cadastroEndereco.tipoLogradouro());
        assertThat(cadastroEnderecoCapturado.logradouro()).isEqualTo(cadastroEndereco.logradouro());
        assertThat(cadastroEnderecoCapturado.numero()).isEqualTo(cadastroEndereco.numero());
        assertThat(cadastroEnderecoCapturado.bairro()).isEqualTo(cadastroEndereco.bairro());
        assertThat(cadastroEnderecoCapturado.cidade()).isEqualTo(cadastroEndereco.cidade());
        assertThat(cadastroEnderecoCapturado.uf()).isEqualTo(cadastroEndereco.uf());

        verify(enderecoService, times(1)).salvarEndereco(cadastroEndereco);
    }

    @Test
    @DisplayName("Deve alterar o endereco de Pessoa com sucesso quando pessoa existir")
    void alterarEnderecoDePessoa_quandoPessoaExistir_entaoAlteraEnderecoComSucesso() {
        Integer id = 1;
        when(pessoaDao.selecionarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco("RUA", "SAO JOSE", 20, "VILA NOVA", null, null);

        emTeste.alterarEnderecoDePessoa(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<RequisicaoAlteracaoEndereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(RequisicaoAlteracaoEndereco.class);
        verify(enderecoService, times(1)).alterarEndereco(anyInt(), enderecoArgumentCaptor.capture());

        RequisicaoAlteracaoEndereco requisicaoEnderecoCapturada = enderecoArgumentCaptor.getValue();

        assertThat(requisicaoEnderecoCapturada.tipoLogradouro()).isEqualTo(requisicaoAlteracaoEndereco.tipoLogradouro());
        assertThat(requisicaoEnderecoCapturada.logradouro()).isEqualTo(requisicaoAlteracaoEndereco.logradouro());
        assertThat(requisicaoEnderecoCapturada.numero()).isEqualTo(requisicaoAlteracaoEndereco.numero());
        assertThat(requisicaoEnderecoCapturada.bairro()).isEqualTo(requisicaoAlteracaoEndereco.bairro());
    }

    @Test
    @DisplayName("excluirPessoa: Deve excluir Pessoa com sucesso quando ID informado existir")
    void excluirPessoa_quandoIdExistir_entaoExcluiPessoaComSucesso() {
        Integer id = 1;

        when(pessoaDao.existePessoa(id)).thenReturn(true);

        emTeste.excluirPessoa(id);

        verify(pessoaDao).excluirPessoa(id);
    }

    @Test
    @DisplayName("excluirPessoa: Deve lancar excecao quando ID informado nao existir")
    void excluirPessoa_quandoIdNaoExistir_entaoLancaExcecao() {
        Integer id = 1;

        when(pessoaDao.existePessoa(id)).thenReturn(false);

        assertThatThrownBy(() -> emTeste.excluirPessoa(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .satisfies(exception -> {
                    RecursoNaoEncontradoException ex = (RecursoNaoEncontradoException) exception;
                    ProblemDetail problemDetail = ex.problemDetail();
                    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(problemDetail.getTitle()).isEqualTo("Recurso nao encontrado");
                    assertThat(problemDetail.getDetail()).isEqualTo("Pessoa com id [%s] nao encontrada".formatted(id));
                    assertThat(problemDetail.getProperties()).containsKey("timestamp");
                });

        verify(pessoaDao, never()).excluirPessoa(id);
    }

    @Test
    @DisplayName("salvarFotos: Deve salvar as fotos enviadas com sucesso quando Pessoa existir")
    void salvarFotos_quandoPessoaExistir_entaoSalvaAsFotosComSucesso() {
        Integer id = 1;
        MultipartFile[] fotos = new MultipartFile[]{ mock(MultipartFile.class) };

        when(pessoaDao.buscarPessoa(id)).thenReturn(Optional.of(pessoa1));

        emTeste.salvarFotos(id, fotos);

        verify(fotoPessoaService, times(1)).salvarFotosDePessoa(pessoa1, fotos);

    }

    @Test
    @DisplayName("salvarFotos: Deve lancar excecao quando Pessoa informada nao existir")
    void salvarFotos_quandoPessoaNaoExistir_entaoLancaExcecao() {
        Integer id = 8888;
        MultipartFile[] fotos = new MultipartFile[]{ mock(MultipartFile.class) };

        when(pessoaDao.buscarPessoa(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emTeste.salvarFotos(id, fotos))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .satisfies(exception -> {
                    RecursoNaoEncontradoException ex = (RecursoNaoEncontradoException) exception;
                    ProblemDetail problemDetail = ex.problemDetail();
                    assertThat(problemDetail.getTitle()).isEqualTo("Recurso nao encontrado");
                    assertThat(problemDetail.getDetail()).isEqualTo("Pessoa nao encontrada".formatted(id));
                    assertThat(problemDetail.getProperties()).containsKey("timestamp");
                });
    }

    @Test
    @DisplayName("recuperarFotos: Deve retornar as fotos da Pessoa informada com sucesso")
    void recuperarFotos_quandoPessoaExistir_entaoRetornarFotosComSucesso() {
        Integer id = 1;

        when(pessoaDao.buscarPessoa(id)).thenReturn(Optional.of(pessoa1));

        emTeste.recuperarFotos(id);

        verify(fotoPessoaService, times(1)).recuperarFotosDePessoa(pessoa1);
    }

    @Test
    @DisplayName("recuperarFotos: Deve lancar excecao quando pessoa nao existir")
    void recuperarFotos_quandoPessoaNaoExistir_entaoLancaExecao() {
        Integer id = 8888;

        when(pessoaDao.buscarPessoa(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emTeste.recuperarFotos(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .satisfies(exception -> {
                    RecursoNaoEncontradoException ex = (RecursoNaoEncontradoException) exception;
                    ProblemDetail problemDetail = ex.problemDetail();
                    assertThat(problemDetail.getTitle()).isEqualTo("Recurso nao encontrado");
                    assertThat(problemDetail.getDetail()).isEqualTo("Pessoa nao encontrada".formatted(id));
                    assertThat(problemDetail.getProperties()).containsKey("timestamp");
                });
    }
}