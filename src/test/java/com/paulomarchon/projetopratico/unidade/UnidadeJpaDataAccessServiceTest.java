package com.paulomarchon.projetopratico.unidade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UnidadeJpaDataAccessServiceTest {

    private UnidadeJpaDataAccessService emTeste;
    private AutoCloseable autoCloseable;
    @Mock UnidadeRepository unidadeRepository;

    @BeforeEach
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emTeste = new UnidadeJpaDataAccessService(unidadeRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void buscarTodasUnidades(){
        Page<Unidade> page = mock(Page.class);
        List<Unidade> unidades = List.of(new Unidade());
        when(page.getContent()).thenReturn(unidades);
        when(unidadeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Unidade> resultadoEsperado = emTeste.buscarTodasUnidades(PageRequest.ofSize(20));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(unidadeRepository, times(1)).findAll(pageableCaptor.capture());

        assertThat(resultadoEsperado.getContent()).isEqualTo(unidades);
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.ofSize(20));
    }

    @Test
    void delecionarUnidadePorId(){
        Integer id = 1;

        emTeste.selecionarUnidadePorId(id);

        verify(unidadeRepository).findById(id);
    }

    @Test
    void cadastrarUnidade(){
        Unidade unidade = new Unidade("UNIDADE DE FISCALIZACAO", "UF", null);

        emTeste.cadastrarUnidade(unidade);

        verify(unidadeRepository).save(unidade);
    }

    @Test
    void alterarUnidade(){
        Unidade unidade = new Unidade("UNIDADE DE FISCALIZACAO", "UF", null);

        emTeste.alterarUnidade(unidade);

        verify(unidadeRepository).save(unidade);
    }

    @Test
    void excluirUnidade(){
        Integer id = 1;

        emTeste.excluirUnidade(id);

        verify(unidadeRepository).deleteById(id);
    }

    @Test
    void existeUnidade(){
        Integer id = 1;

        emTeste.existeUnidade(id);

        verify(unidadeRepository).existsById(id);
    }
}
