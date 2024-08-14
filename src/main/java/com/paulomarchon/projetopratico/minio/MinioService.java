package com.paulomarchon.projetopratico.minio;

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

    public void enviarImagens(String nomeBucket, List<SnowballObject> imagens) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.uploadSnowballObjects(UploadSnowballObjectsArgs.builder()
                .bucket(nomeBucket)
                .objects(imagens)
                .build()
        );
    }

    public void excluirImagens(String nomeBucket, List<DeleteObject> imagens) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(nomeBucket).objects(imagens).build()
                );

        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println(error);
        }
    }

    public List<String> recuperarImagens(String bucketNome, List<String> imagemHash) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<String> linksTemporarios = new ArrayList<>();
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", "image/jpeg");

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
    }
}

