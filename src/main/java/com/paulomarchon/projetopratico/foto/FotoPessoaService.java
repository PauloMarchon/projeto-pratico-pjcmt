package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.exception.FalhaNoServicoS3Exception;
import com.paulomarchon.projetopratico.minio.MinioBuckets;
import com.paulomarchon.projetopratico.minio.MinioService;
import com.paulomarchon.projetopratico.pessoa.Pessoa;
import io.minio.SnowballObject;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FotoPessoaService {
    private final FotoPessoaDao fotoPessoaDao;
    private final MinioBuckets minioBuckets;
    private final MinioService minioService;

    public FotoPessoaService(@Qualifier("foto-pessoa-jpa") FotoPessoaDao fotoPessoaDao, MinioBuckets minioBuckets, MinioService minioService) {
        this.fotoPessoaDao = fotoPessoaDao;
        this.minioBuckets = minioBuckets;
        this.minioService = minioService;
    }

    public List<String> recuperarTodasFotosDePessoa(Pessoa pessoa) {
        List<String> imagensHash =
                fotoPessoaDao.recuperarTodasFotosDePessoa(pessoa).stream()
                                .map(FotoPessoa::getHash)
                                .collect(Collectors.toList());

        return minioService.recuperarImagens(minioBuckets.getFoto(), imagensHash);
    }

    @Transactional
    public void salvarFotosDePessoa(Pessoa pessoa, List<MultipartFile> fotos) {
        List<FotoPessoa> fotosPessoa = new ArrayList<>();
        List<SnowballObject> objetosS3 = new ArrayList<>();

        for (MultipartFile foto : fotos) {
            String hash = UUID.randomUUID().toString();

            fotosPessoa.add(preparaFotoDePessoa(pessoa, hash));
            objetosS3.add(preparaFotosParaServidorS3(hash, foto));
        }

        try {
            minioService.enviarImagens(minioBuckets.getFoto(), objetosS3);

            fotoPessoaDao.adicionarFotosDePessoa(fotosPessoa);
        } catch (FalhaNoServicoS3Exception e) {
            throw new FalhaNoServicoS3Exception("Erro ao adicionar fotos de pessoa", e);
        }
    }

    private  FotoPessoa preparaFotoDePessoa(Pessoa pessoa, String hash) {
        return new FotoPessoa(
                        pessoa,
                        LocalDate.now(),
                        minioBuckets.getFoto(),
                        hash
        );
    }

    private SnowballObject preparaFotosParaServidorS3(String hash, MultipartFile foto) {
        try {
            return new SnowballObject(
                            hash,
                            new ByteArrayInputStream(foto.getBytes()),
                            foto.getSize(),
                            ZonedDateTime.now()
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo de foto");
        }
    }

    @Transactional
    public void excluirFotoDePessoa(List<String> imagemHash) {
        List<DeleteObject> objects = new LinkedList<>();

        for (String hash : imagemHash) {
            if (fotoPessoaDao.existeFotoDePessoaPorHash(hash))
                objects.add(new DeleteObject(hash));
        }

        try {
            minioService.excluirImagens(minioBuckets.getFoto(), objects);

            fotoPessoaDao.excluirFotoDePessoaPorHash(imagemHash);
        } catch (FalhaNoServicoS3Exception e) {
            throw new FalhaNoServicoS3Exception("Erro ao excluir fotos de pessoa", e);
        }
    }
}
