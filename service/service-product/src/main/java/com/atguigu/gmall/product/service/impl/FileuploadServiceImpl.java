package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MioioProperties;
import com.atguigu.gmall.product.service.FileuploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class FileuploadServiceImpl implements FileuploadService {


   /* @Value("${app.minio.endpoint}")
    String endpoint;
    @Value("${app.minio.ak}")
    String ak;
    @Value("${app.minio.sk}")
    String sk;
    @Value("${app.minio.bucketName}")
    String bucketName;*/




    /*
        文件上传
     */

    @Autowired
    MinioClient minioClient;

    @Autowired
    MioioProperties mioioProperties;


    @Override
    public String upload(MultipartFile file) throws Exception {

//        //1.创建一个MinioClient
//        MinioClient minioClient = new MinioClient(endpoint, ak, sk);
        //2.判断桶是否存在
        boolean gmall = minioClient.bucketExists(mioioProperties.getBucketName());
        //不存在创建桶
        if (!gmall) {
            minioClient.makeBucket(mioioProperties.getBucketName());
        }

        //3.给桶创建文件

        String formatDate = DateUtil.formatDate(new Date());
        //获取文件名(唯一文件名)
        String originalFilename = UUID.randomUUID().toString().replace("-", "") + "-" + file.getOriginalFilename();
        //获取文件流
        InputStream inputStream = file.getInputStream();
        //获取文件类型
        String contentType = file.getContentType();
        PutObjectOptions putObjectOptions = new PutObjectOptions(file.getSize(), -1L);
        putObjectOptions.setContentType(contentType);
        minioClient.putObject(mioioProperties.getBucketName(), formatDate + "/" + originalFilename, inputStream, putObjectOptions);

        String url = mioioProperties.getEndpoint() + "/" + mioioProperties.getBucketName() + "/" + formatDate + "/" + originalFilename;

        return url;
    }
}
