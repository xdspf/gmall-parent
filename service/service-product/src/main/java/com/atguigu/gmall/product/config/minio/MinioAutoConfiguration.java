package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
Minio配置类
 */
@Configuration
public class MinioAutoConfiguration {

    @Autowired
    MioioProperties mioioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = new MinioClient(mioioProperties.getEndpoint(),
                mioioProperties.getAk(), mioioProperties.getSk());
        String bucketName = mioioProperties.getBucketName();
        if (!minioClient.bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }
        return minioClient;
    }

}
