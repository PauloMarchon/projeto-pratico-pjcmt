package com.paulomarchon.projetopratico.cidade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class CidadeRepositoryTest {

    @Autowired
    CidadeRepository cidadeRepository;

    @BeforeEach
    void setUp() {
        cidadeRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar Cidade com sucesso quando Nome e Uf informados existir")
    void findByNomeIgnoreCaseAndUf_quandoCidadeUfExistir_entaoDeveRetornarComSucesso() {
        Cidade c1 = new Cidade("SAO PAULO", UF.SP);
        cidadeRepository.save(c1);

        Optional<Cidade> cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("SAO paulo", UF.SP);

        assertThat(cidade.isPresent()).isTrue();
        assertThat(cidade.get().getNome()).isEqualTo("SAO PAULO");
        assertThat(cidade.get().getUf()).isEqualTo(UF.SP);
    }
    //salvarFotos_quandoPessoaNaoEncontrada_entaoLancaExcecao
    @Test
    @DisplayName("Deve retornar vazio quando Cidade com Nome e Uf informados nao existir")
    void findByNomeIgnoreCaseAndUf_quandoCidadeUfNaoExistir_entaoDeveRetornarVazio() {
        Optional<Cidade> cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("sao paulo", UF.SP);

        assertThat(cidade.isEmpty()).isTrue();
    }
}
