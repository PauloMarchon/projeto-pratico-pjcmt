package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import com.paulomarchon.projetopratico.pessoa.Pessoa;
import com.paulomarchon.projetopratico.pessoa.PessoaRepository;
import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class FotoPessoaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private FotoPessoaRepository fotoPessoaRepository;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() throws Exception {
        fotoPessoaRepository.deleteAll();
        pessoaRepository.deleteAll();
    }

    @Test
    void deveRetornarTodasAsFotosBuscandoPorPessoa() throws Exception {
        pessoa = new Pessoa("AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoaRepository.save(pessoa);

        FotoPessoa fotoPessoa1 = new FotoPessoa(pessoa, LocalDate.now(), "foto", UUID.randomUUID().toString());
        FotoPessoa fotoPessoa2 = new FotoPessoa(pessoa, LocalDate.now(), "foto", UUID.randomUUID().toString());
        FotoPessoa fotoPessoa3 = new FotoPessoa(pessoa, LocalDate.now(), "foto", UUID.randomUUID().toString());
        fotoPessoaRepository.save(fotoPessoa1);
        fotoPessoaRepository.save(fotoPessoa2);
        fotoPessoaRepository.save(fotoPessoa3);

        List<FotoPessoa> resultadoAtual = fotoPessoaRepository.findAllByPessoa(pessoa);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.size()).isEqualTo(3);
    }

    @Test
    void deveDeletarTodasAsFotosComOsHashesInformados() {
        pessoa = new Pessoa("AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");
        pessoaRepository.save(pessoa);

        String hash1 = UUID.randomUUID().toString();
        String hash2 = UUID.randomUUID().toString();
        String hash3 = UUID.randomUUID().toString();

        FotoPessoa fotoPessoa1 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hash1);
        FotoPessoa fotoPessoa2 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hash2);
        FotoPessoa fotoPessoa3 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hash3);
        fotoPessoaRepository.save(fotoPessoa1);
        fotoPessoaRepository.save(fotoPessoa2);
        fotoPessoaRepository.save(fotoPessoa3);

        List<String> hashesAlvos = List.of(hash1, hash2);

        fotoPessoaRepository.deleteAllByHashIn(hashesAlvos);

        List<FotoPessoa> resultadoAtual = fotoPessoaRepository.findAll();

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.size()).isEqualTo(1);
    }
}
