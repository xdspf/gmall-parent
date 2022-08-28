package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;


//@SpringBootTest
public class FileUploader {

  public static void main(String[] args) throws Exception {
    try {
      // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
      MinioClient minioClient = new MinioClient("http://192.168.200.100:9000", "admin", "admin123456");

      // 检查存储桶是否已经存在
      boolean isExist = minioClient.bucketExists("gmall");
      if (isExist) {
        System.out.println("Bucket already exists.");
      } else {
        // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
        minioClient.makeBucket("gmall");
      }

      // 使用putObject上传一个文件到存储桶中。
      //String bucketName:桶名
      // String objectName:对象名，也就是文件名    C:\Users\SPF\Desktop\跨域举例.png
      // PutObjectOptions options:文件流
      // Object data：上传的参数设置

      FileInputStream fileInputStream = new FileInputStream("C:\\Users\\SPF\\Desktop\\1.png");
      PutObjectOptions putObjectOptions = new PutObjectOptions(fileInputStream.available(),-1l);
      putObjectOptions.setContentType("image/png");
      minioClient.putObject("gmall","1.png",fileInputStream,putObjectOptions);
      System.out.println("上传成功");
    } catch (MinioException e) {
      System.out.println("上传失败：" + e);
    }
  }
}