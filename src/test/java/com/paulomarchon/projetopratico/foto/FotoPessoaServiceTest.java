package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.minio.MinioBuckets;
import com.paulomarchon.projetopratico.minio.MinioService;
import com.paulomarchon.projetopratico.pessoa.Pessoa;
import com.paulomarchon.projetopratico.pessoa.SexoPessoa;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        MultipartFile fotoMock = mock(MultipartFile.class);
        String conteudoDaFoto = UUID.randomUUID().toString();
        InputStream inputStream = new ByteArrayInputStream(conteudoDaFoto.getBytes());

        when(fotoMock.getInputStream()).thenReturn(inputStream);
        when(fotoMock.getSize()).thenReturn((long) conteudoDaFoto.getBytes().length);
        when(fotoMock.getContentType()).thenReturn("image/jpeg");

        MultipartFile[] fotos = new MultipartFile[]{fotoMock};

        emTeste.salvarFotosDePessoa(pessoa, fotos);

        //FotoPessoaDao
        ArgumentCaptor<FotoPessoa> fotoPessoaArgumentCaptor = ArgumentCaptor.forClass(FotoPessoa.class);
        verify(fotoPessoaDao).adicionarFotoDePessoa(fotoPessoaArgumentCaptor.capture());
        FotoPessoa fotoPessoaCapturada = fotoPessoaArgumentCaptor.getValue();

        assertThat(fotoPessoaCapturada).isNotNull();
        assertThat(fotoPessoaCapturada.getBucket()).isEqualTo("foto-pessoa");
        assertThat(fotoPessoaCapturada.getData()).isEqualTo(LocalDate.now());
        assertThat(fotoPessoaCapturada.getPessoa()).isEqualTo(pessoa);

        //Minio
        ArgumentCaptor<PutObjectArgs> putObjectArgsArgumentCaptor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioService).enviarImagens(putObjectArgsArgumentCaptor.capture());
        PutObjectArgs putObjectArgsCapturado = putObjectArgsArgumentCaptor.getValue();

        assertThat(putObjectArgsCapturado).isNotNull();
        assertThat(putObjectArgsCapturado.bucket()).isEqualTo("foto-pessoa");
        assertThat(putObjectArgsCapturado.object()).isNotBlank();
        assertThat(putObjectArgsCapturado.contentType()).isEqualTo("image/jpeg");
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
