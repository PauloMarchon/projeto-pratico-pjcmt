package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.endereco.EnderecoService;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.exception.RecursoNaoEncontradoException;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoAlteracaoPessoa;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PessoaServiceTest {

    @Mock
    private PessoaDao pessoaDao;
    @Mock
    private EnderecoService enderecoService;
    private PessoaService emTeste;
    private final PessoaDtoMapper pessoaDtoMapper = new PessoaDtoMapper();

    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private Pessoa pessoa3;

    @BeforeEach
    void setUp() {
        emTeste = new PessoaService(pessoaDao, pessoaDtoMapper, enderecoService);

        Endereco endereco1 = new Endereco(1,"RUA", "SAO JOSE", 20, "VILA NOVA", null);

        pessoa1 = new Pessoa(1, "MARCELO FERNANDES", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoa1.setEndereco(endereco1);
        pessoa2 = new Pessoa(2, "MARCELO FERNANDES", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoa3 = new Pessoa(3, "AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
    }

    @Test
    void deveBuscarTodasPessoas() {
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
    void deveBuscarPessoaPorNome() {
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
    void deveBuscarPessoaPorNomeVazioPorNaoObterCorrespondencia() {
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
    void deveCadastrarPessoaCorretamente() {
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
    void deveRetornarPessoaCadastradaCorretamente() {
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
    void deveAlterarTodasAsPropriedadesDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarSomenteNomeDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarSomenteDataDeNascimentoDePessoa() {
        Integer id = 1;
        LocalDate dataNascimento = LocalDate.of(1990, 10, 12);
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarSomenteSexoDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarSomenteNomeDaMaeDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarSomenteNomeDoPaiDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveCadastrarEnderecoDePessoaCorretamente() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveAlterarEnderecoDePessoa() {
        Integer id = 1;
        when(pessoaDao.buscarPessoaPorReferenciaDeId(id)).thenReturn(pessoa1);

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
    void deveExcluirPessoaComSucesso() {
        Integer id = 1;

        when(pessoaDao.existePessoa(id)).thenReturn(true);

        emTeste.excluirPessoa(id);

        verify(pessoaDao).excluirPessoa(id);
    }

    @Test
    void deveFalharAoTentarExcluirPessoaComIdInexistente() {
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
}