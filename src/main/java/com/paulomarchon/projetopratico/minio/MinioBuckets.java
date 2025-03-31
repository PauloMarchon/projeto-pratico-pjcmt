package com.paulomarchon.projetopratico.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio.buckets")
public class MinioBuckets {

    private String bucketFotoPessoa;

    public String getBucketFotoPessoa() {
        return bucketFotoPessoa;
    }

    public void setBucketFotoPessoa(String bucketFotoPessoa) {
        this.bucketFotoPessoa = bucketFotoPessoa;
    }
}
