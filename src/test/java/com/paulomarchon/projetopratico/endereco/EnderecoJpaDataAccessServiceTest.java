package com.paulomarchon.projetopratico.endereco;

import com.paulomarchon.projetopratico.cidade.Cidade;
import com.paulomarchon.projetopratico.cidade.UF;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;


public class EnderecoJpaDataAccessServiceTest {

    private EnderecoJpaDataAccessService emTeste;
    private AutoCloseable autoCloseable;
    @Mock EnderecoRepository enderecoRepository;

    @BeforeEach
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emTeste = new EnderecoJpaDataAccessService(enderecoRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selecionarEnderecoPorReferenciaDeId(){
        Integer id = 1;

        emTeste.selecionarEnderecoPorReferenciaDeId(id);

        verify(enderecoRepository).getReferenceById(id);
    }

    @Test
    void salvarEndereco(){
        Cidade cidade = new Cidade("SAO PALO", UF.SP);
        Endereco endereco = new Endereco("RUA", "SAO JOAO", 120, "CENTRO", cidade);

        emTeste.salvarEndereco(endereco);

        verify(enderecoRepository).save(endereco);
    }

    @Test
    void alterarEndereco(){
        Cidade cidade = new Cidade("SAO PALO", UF.SP);
        Endereco endereco = new Endereco("RUA", "SAO JOAO", 120, "CENTRO", cidade);

        emTeste.alterarEndereco(endereco);

        verify(enderecoRepository).save(endereco);
    }
}
