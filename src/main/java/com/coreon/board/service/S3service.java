package com.coreon.board.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3service {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3service(AmazonS3 amazonS3,
                     @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    // (기존) 단순 업로드: 버킷 루트에 저장
    public String uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + safeName(file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest request = new PutObjectRequest(
                bucketName, key, file.getInputStream(), metadata
        );

        amazonS3.putObject(request);
        return amazonS3.getUrl(bucketName, key).toString();
    }

    // ✅ (추가) board용 업로드: board/{boardId}/ 하위로 저장
    public String upload(Long boardId, MultipartFile file) {
        try {
            String original = safeName(file.getOriginalFilename());
            String key = "board/" + boardId + "/" + UUID.randomUUID() + "_" + original;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest request = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), metadata
            );

            amazonS3.putObject(request);
            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }

    // 파일명 null/이상값 방지
    private String safeName(String name) {
        if (name == null || name.isBlank()) return "file";
        // 윈도우 경로 제거 + 공백/위험 문자 단순 치환
        name = name.replace("\\", "/");
        name = name.substring(name.lastIndexOf('/') + 1);
        return name.replaceAll("[\\r\\n]", "_");
    }
}
