package com.paulomarchon.projetopratico.minio;

import com.paulomarchon.projetopratico.exception.FalhaNoServicoS3Exception;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void enviarImagens(PutObjectArgs objectArgs) {
        try{
            minioClient.putObject(
                    objectArgs
            );
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            throw new FalhaNoServicoS3Exception("Falha ao enviar imagens ao Minio", e);
        }
    }

    public void excluirImagens(String nomeBucket, List<DeleteObject> imagens) {
        try{
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(nomeBucket).objects(imagens).build()
                    );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.out.println(error);
            }
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            throw new FalhaNoServicoS3Exception("Falha ao excluir imagens do Minio", e);
        }
    }

    public List<String> recuperarImagens(String bucketNome, List<String> imagemHash) {
        List<String> linksTemporarios = new ArrayList<>();
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", "image/jpeg");

        try{
            for (String hash : imagemHash) {
                String url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.HEAD)
                                .bucket(bucketNome)
                                .object(hash)
                                .expiry(5, TimeUnit.MINUTES)
                                .extraQueryParams(reqParams)
                                .build()
                );
                linksTemporarios.add(url);
            }
            return linksTemporarios;
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            throw new FalhaNoServicoS3Exception("Falha ao recuperar imagens do Minio", e);
        }
    }
}

