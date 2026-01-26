package com.coreon.notice.service;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

@Service
public class S3service {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String prefix;

    public S3service(
            AmazonS3 amazonS3,
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${custom.s3.prefix:}") String prefix
    ) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;

        if (prefix == null || prefix.isBlank()) {
            this.prefix = "";
        } else {
            String p = prefix.trim();
            this.prefix = p.endsWith("/") ? p : (p + "/");
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = prefix + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));

        return key; // ✅ DB에는 URL 말고 key 저장
    }


    // ============================
    // ✅ Presigned Download URL 발급
    // ============================
    public String generatePresignedDownloadUrl(String storedUrl, String originalName, int expiresSeconds) {
        String key = extractKeyFromStoredUrl(storedUrl);

        Date expiration = new Date(System.currentTimeMillis() + (expiresSeconds * 1000L));

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        // 브라우저에서 "다운로드"로 떨어지도록 Content-Disposition 세팅
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        overrides.setContentType("application/octet-stream");

        String encoded = urlEncode(originalName);
        // filename + filename* 같이 넣으면 대부분의 브라우저에서 한글 파일명도 안정적으로 처리됨
        overrides.setContentDisposition(
                "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded
        );

        req.setResponseHeaders(overrides);

        URL presigned = amazonS3.generatePresignedUrl(req);
        return presigned.toString();
    }

    public void deleteFileByUrl(String url) {
        String key = extractKeyFromStoredUrl(url);
        amazonS3.deleteObject(bucketName, key);
    }

    // ============================
    // 내부 유틸
    // ============================
    private String extractKeyFromStoredUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isBlank()) {
            throw new IllegalArgumentException("storedUrl is empty");
        }

        // 이미 key만 저장하는 방식이라면(예: notice/xxx.png) 그대로 key로 취급
        if (!storedUrl.startsWith("http://") && !storedUrl.startsWith("https://")) {
            return storedUrl.startsWith("/") ? storedUrl.substring(1) : storedUrl;
        }

        try {
            URL u = new URL(storedUrl);
            String path = u.getPath();        // 예: /notice/uuid_name.txt  또는 /coreon/notice/...
            if (path.startsWith("/")) path = path.substring(1);

            // path-style URL 이면 맨 앞에 bucketName이 붙을 수 있음: coreon/notice/...
            if (path.startsWith(bucketName + "/")) {
                path = path.substring((bucketName + "/").length());
            }
            return path;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid storedUrl: " + storedUrl, e);
        }
    }

    private String urlEncode(String s) {
        try {
            // URLEncoder는 공백을 +로 바꾸므로 %20으로 치환
            return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            return s;
        }
    }
}
