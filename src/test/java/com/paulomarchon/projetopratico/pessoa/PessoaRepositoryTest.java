package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PessoaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() throws Exception {
        pessoaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar os nomes correspondentes ao informado")
    void findByNomeContainingIgnoreCase_quandoBuscadoPorNome_entaoRetornaNomesCorrespondentes() {
        Pessoa pessoa1 = new Pessoa("AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        Pessoa pessoa2 = new Pessoa("MARCELO MEDINA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        Pessoa pessoa3 = new Pessoa("MARCELO FERNANDES", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoaRepository.save(pessoa1);
        pessoaRepository.save(pessoa2);
        pessoaRepository.save(pessoa3);

        String nomeProcurado = "MARCELO";
        Pageable pageable = PageRequest.ofSize(5);

        Page<Pessoa> resultadoEsperado = pessoaRepository.findByNomeContainingIgnoreCase(nomeProcurado, pageable);

        assertThat(resultadoEsperado).isNotNull();
        assertThat(resultadoEsperado.getTotalElements()).isEqualTo(2);
        assertThat(resultadoEsperado.getTotalPages()).isEqualTo(1);
        assertThat(resultadoEsperado.getContent().getFirst().getNome()).isEqualTo(pessoa2.getNome());
        assertThat(resultadoEsperado.getContent().getLast().getNome()).isEqualTo(pessoa3.getNome());
    }
}
