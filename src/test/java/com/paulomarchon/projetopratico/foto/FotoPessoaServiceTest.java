package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.exception.FalhaNoServicoS3Exception;
import com.paulomarchon.projetopratico.minio.MinioBuckets;
import com.paulomarchon.projetopratico.minio.MinioService;
import com.paulomarchon.projetopratico.pessoa.Pessoa;
import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FotoPessoaServiceTest {

    @Mock
    private FotoPessoaDao fotoPessoaDao;
    @Mock
    private MinioBuckets minioBuckets;
    @Mock
    private MinioService minioService;
    private FotoPessoaService emTeste;

    private Pessoa pessoa;
    private List<MultipartFile> fotos;

    @BeforeEach
    void setUp() {
        emTeste = new FotoPessoaService(fotoPessoaDao, minioBuckets, minioService);

        pessoa = new Pessoa("AFONSO SOUZA", LocalDate.now(), SexoPessoa.MASCULINO, "REGINA", "AFONSO");

        when(minioBuckets.getBucketFotoPessoa()).thenReturn("foto-pessoa");
    }

    @Test
    @DisplayName("Deve recuperar todas as fotos da pessoa informada com sucesso")
    void recuperarFotosDePessoa_quandoPessoaExistir_entaoRecuperaTodasFotosComSucesso() {
        String hashFoto1 = UUID.randomUUID().toString();
        String hashFoto2 = UUID.randomUUID().toString();
        String hashFoto3 = UUID.randomUUID().toString();

        FotoPessoa fotoPessoa1 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hashFoto1);
        FotoPessoa fotoPessoa2 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hashFoto2);
        FotoPessoa fotoPessoa3 = new FotoPessoa(pessoa, LocalDate.now(), "foto", hashFoto3);

        List<FotoPessoa> fotos = List.of(fotoPessoa1, fotoPessoa2, fotoPessoa3);
        List<String> hashesFotos = List.of(hashFoto1, hashFoto2, hashFoto3);

        when(fotoPessoaDao.recuperarTodasFotosDePessoa(pessoa)).thenReturn(fotos);
        when(minioService.recuperarImagens(minioBuckets.getBucketFotoPessoa(), hashesFotos)).thenReturn(hashesFotos);

        List<String> resultadoAtual = emTeste.recuperarFotosDePessoa(pessoa);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual).hasSize(3);
        assertThat(resultadoAtual).containsExactly(hashFoto1, hashFoto2, hashFoto3);
    }

    @Test
    @DisplayName("Deve salvar todas as fotos da pessoa informada quando Pessoa existir")
    void salvarFotosDePessoa_quandoPessoaExistir_entaoSalvaFotosComSucesso() throws IOException {
        MultipartFile foto = mock(MultipartFile.class);
        List<MultipartFile> fotos = List.of(foto);

        String hash = UUID.randomUUID().toString();

        when(minioBuckets.getFoto()).thenReturn(hash);
        when(foto.getBytes()).thenReturn("conteudo".getBytes());
        when(foto.getSize()).thenReturn((long) "conteudo".length());

        doNothing().when(minioService).enviarImagens(anyString(), anyList());
        doNothing().when(fotoPessoaDao).adicionarFotosDePessoa(anyList());

        emTeste.salvarFotosDePessoa(pessoa, fotos);

        verify(minioService, times(1)).enviarImagens(any(PutObjectArgs.class));
        verify(fotoPessoaDao, times(1)).adicionarFotoDePessoa(any(FotoPessoa.class));
    }

    @Test
    @DisplayName("Deve excluir todas as fotos referentes aos hash's informados")
    void excluirFotoDePessoa_quandoHashExistir_entaoExcluiFotoComSucesso() {
        List<String> imagemHash = Arrays.asList("hash1", "hash2", "hash3");

        emTeste.excluirFotoDePessoa(imagemHash);

        verify(minioService, times(1)).excluirImagens(anyString(), anyList());
        verify(fotoPessoaDao, times(1)).excluirFotoDePessoaPorHash(imagemHash);
    }
}
