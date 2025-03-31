package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.pessoa.Pessoa;
import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FotoPessoaJpaDataAccessServiceTest {

    @Mock
    private FotoPessoaRepository fotoPessoaRepository;
    private FotoPessoaJpaDataAccessService emTeste;
    private AutoCloseable autoCloseable;

    private Pessoa pessoa;
    private FotoPessoa fotoPessoa1;
    private FotoPessoa fotoPessoa2;


    @BeforeEach
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emTeste = new FotoPessoaJpaDataAccessService(fotoPessoaRepository);

        pessoa = new Pessoa("AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        fotoPessoa1 = new FotoPessoa(pessoa, LocalDate.now(), "foto", UUID.randomUUID().toString());
        fotoPessoa2 = new FotoPessoa(pessoa, LocalDate.now(), "foto", UUID.randomUUID().toString());
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void recuperarTodasFotosDePessoa() {
        List<FotoPessoa> fotos = List.of(fotoPessoa1, fotoPessoa2);
        when(fotoPessoaRepository.findAllByPessoa(pessoa)).thenReturn(fotos);

        List<FotoPessoa> resultadoAtual = emTeste.recuperarTodasFotosDePessoa(pessoa);

        assertThat(resultadoAtual).isEqualTo(fotos);

        verify(fotoPessoaRepository).findAllByPessoa(pessoa);
    }

    @Test
    void adicionarFotoDePessoa() {
        when(fotoPessoaRepository.save(fotoPessoa1)).thenReturn(fotoPessoa1);

        emTeste.adicionarFotoDePessoa(fotoPessoa1);

        verify(fotoPessoaRepository).save(fotoPessoa1);
    }

    @Test
    void excluirFotoDePessoaPorHash() {
        List<String> hashes = List.of("hash1", "hash2");

        doNothing().when(fotoPessoaRepository).deleteAllByHashIn(hashes);

        emTeste.excluirFotoDePessoaPorHash(hashes);

        verify(fotoPessoaRepository).deleteAllByHashIn(hashes);
    }
}
