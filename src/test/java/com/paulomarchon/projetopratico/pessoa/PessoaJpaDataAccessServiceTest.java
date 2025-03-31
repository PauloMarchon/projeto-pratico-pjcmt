package com.paulomarchon.projetopratico.pessoa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PessoaJpaDataAccessServiceTest {

    @Mock private PessoaRepository pessoaRepository;
    private PessoaJpaDataAccessService emTeste;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emTeste = new PessoaJpaDataAccessService(pessoaRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void buscarTodasPessoas() {
        Page<Pessoa> page = mock(Page.class);
        List<Pessoa> pessoas = List.of(new Pessoa());
        when(page.getContent()).thenReturn(pessoas);
        when(pessoaRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Pessoa> resultadoEsperado = emTeste.buscarTodasPessoas(PageRequest.ofSize(10));

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(pessoaRepository, times(1)).findAll(pageableArgumentCaptor.capture());

        assertThat(resultadoEsperado.getContent()).isEqualTo(pessoas);
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(PageRequest.ofSize(10));
    }

    @Test
    void buscarPessoaPorNome() {
        String nome = "PAULO";
        Page<Pessoa> page = mock(Page.class);
        List<Pessoa> pessoas = List.of(new Pessoa());
        when(page.getContent()).thenReturn(pessoas);
        when(pessoaRepository.findByNomeContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Pessoa> resultadoEsperado = emTeste.buscarPessoaPorNome(nome, PageRequest.ofSize(10));

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(pessoaRepository, times(1)).findByNomeContainingIgnoreCase(eq(nome), pageableArgumentCaptor.capture());

        assertThat(resultadoEsperado).isNotNull();
        assertThat(resultadoEsperado.getContent()).isEqualTo(pessoas);
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(PageRequest.ofSize(10));
    }

    @Test
    void selecionarPessoaPorReferenciaDeId() {
        Integer id = 1;
        Pessoa pessoa = new Pessoa(id,"PAULO", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        when(pessoaRepository.getReferenceById(id)).thenReturn(pessoa);

        Pessoa resultadoAtual = emTeste.selecionarPessoaPorReferenciaDeId(id);

        assertThat(resultadoAtual.getNome()).isEqualTo(pessoa.getNome());

        verify(pessoaRepository).getReferenceById(id);
    }

    @Test
    void cadastrarPessoa() {
        Pessoa pessoa = new Pessoa("PAULO", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        Pessoa resultadoAtual = emTeste.cadastrarPessoa(pessoa);

        assertThat(resultadoAtual.getNome()).isEqualTo(pessoa.getNome());
        assertThat(resultadoAtual.getDataNascimento()).isEqualTo(pessoa.getDataNascimento());
        assertThat(resultadoAtual.getSexo()).isEqualTo(pessoa.getSexo());
        assertThat(resultadoAtual.getNomeMae()).isEqualTo(pessoa.getNomeMae());
        assertThat(resultadoAtual.getNomePai()).isEqualTo(pessoa.getNomePai());

        verify(pessoaRepository, times(1)).save(pessoa);
    }

    @Test
    void alterarPessoa() {
        Pessoa pessoa = new Pessoa("PAULO", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");

        emTeste.alterarPessoa(pessoa);

        verify(pessoaRepository).save(pessoa);
    }

    @Test
    void excluirPessoa() {
        Integer id = 1;

        emTeste.excluirPessoa(id);

        verify(pessoaRepository).deleteById(id);
    }


    @Test
    void verificaSeExistePessoa_quandoPessoaExistir() {
        Integer id = 1;
        when(pessoaRepository.existsById(id)).thenReturn(true);

        boolean existePessoa = emTeste.existePessoa(id);

        assertThat(existePessoa).isTrue();

        verify(pessoaRepository).existsById(id);
    }

    @Test
    void verificaSeExistePessoa_quandoPessoaNaoExistir() {
        Integer id = 1;
        when(pessoaRepository.existsById(id)).thenReturn(false);

        boolean existePessoa = emTeste.existePessoa(id);

        assertThat(existePessoa).isFalse();

        verify(pessoaRepository).existsById(id);
    }
}
