package com.paulomarchon.projetopratico.foto;

import com.paulomarchon.projetopratico.exception.FalhaNoServicoS3Exception;
import com.paulomarchon.projetopratico.minio.MinioBuckets;
import com.paulomarchon.projetopratico.minio.MinioService;
import com.paulomarchon.projetopratico.pessoa.Pessoa;
import io.minio.PutObjectArgs;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    public List<String> recuperarFotosDePessoa(Pessoa pessoa) {
        List<String> imagensHash =
                fotoPessoaDao.recuperarTodasFotosDePessoa(pessoa).stream()
                                .map(FotoPessoa::getHash)
                                .collect(Collectors.toList());

        return minioService.recuperarImagens(minioBuckets.getBucketFotoPessoa(), imagensHash);
    }

    @Transactional
    public void salvarFotosDePessoa(Pessoa pessoa, MultipartFile[] fotos) {
        for (MultipartFile foto : fotos) {
            try {
                String hash = UUID.randomUUID().toString();

                fotoPessoaDao.adicionarFotoDePessoa(
                        new FotoPessoa(
                                pessoa,
                                LocalDate.now(),
                                minioBuckets.getBucketFotoPessoa(),
                                hash
                        )
                );

                minioService.enviarImagens(
                        PutObjectArgs.builder()
                                .bucket(minioBuckets.getBucketFotoPessoa())
                                .object(hash)
                                .stream(foto.getInputStream(), foto.getSize(), -1)
                                .contentType(foto.getContentType())
                                .build()
                );

            } catch (FalhaNoServicoS3Exception | IOException e ) {
                throw new FalhaNoServicoS3Exception("Erro ao adicionar fotos de pessoa", e);
            }
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
            minioService.excluirImagens(minioBuckets.getBucketFotoPessoa(), objects);

            fotoPessoaDao.excluirFotoDePessoaPorHash(imagemHash);
        } catch (FalhaNoServicoS3Exception e) {
            throw new FalhaNoServicoS3Exception("Erro ao excluir fotos de pessoa", e);
        }
    }
}
