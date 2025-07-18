package com.wetrip.service;


import jakarta.annotation.PostConstruct;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;


    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /**
     * S3에 업로드 가능한 Presigned URL 생성
     * 동시에 S3에 실제로 저장될 고유 파일명(UUID 기반)을 생성해서 함께 반환
     * @return Map {"uploadUrl": URL, "fileName": 고유파일명}
     */

    public Map<String, String> generatePresignedUploadUrl(
        String prefix,  //파일 경로의 앞 부분
        String extension,  //파일의 확장자
        Duration expireDuration) {

        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        String filePath = String.format("%s/%s", prefix, uniqueFileName);
        String contentType = String.format("image/%s", extension);

        // PutObjectRequest 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .contentType(contentType)
            .build();

        // PresignedPutObjectRequest 생성
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(builder ->
            builder
                .signatureDuration(expireDuration)
                .putObjectRequest(putObjectRequest)
        );

        return Map.of("uploadUrl", presignedRequest.url().toString(),
            "fileName", uniqueFileName);
    }

    public URL generatePresignedDownloadUrl(
        String prefix,
        String fileName,
        Duration expireDuration) {

        String filePath = String.format("%s/%s", prefix, fileName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(builder ->
            builder
                .signatureDuration(expireDuration)
                .getObjectRequest(getObjectRequest)
        );

        return presignedRequest.url();
    }

    public void deleteObject(String prefix, String fileName) {
        String filePath = String.format("%s/%s", prefix, fileName);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build();

        s3Client.deleteObject(deleteRequest);
    }
}
