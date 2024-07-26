package com.paulomarchon.projetopratico.cidade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class CidadeJpaDataAccessServiceTest {

    private CidadeJpaDataAccessService emTeste;
    private AutoCloseable autoCloseable;
    @Mock private CidadeRepository cidadeRepository;

    @BeforeEach
    void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emTeste = new CidadeJpaDataAccessService(cidadeRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void buscarCidade() {
        String nome = "SAO PAULO";
        UF uf = UF.SP;

        Cidade cidade = new Cidade(nome, uf);

        emTeste.buscarCidade(nome, uf);

        verify(cidadeRepository).findByNomeIgnoreCaseAndUf(nome, uf);
    }

    @Test
    void cadastrarCidade() {
        Cidade cidade = new Cidade(
                "SAO PAULO",
                UF.SP
        );

         emTeste.cadastrarCidade(cidade);

         verify(cidadeRepository).save(cidade);
    }
}
