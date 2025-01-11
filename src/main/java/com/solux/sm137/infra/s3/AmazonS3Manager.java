package com.solux.sm137.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.solux.sm137.infra.config.AmazonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    // S3에 여러 이미지 업로드
    public List<String> uploadFiles(List<MultipartFile> files) {

        // 반환 받을 이미지 url 리스트
        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile file : files) {
            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            // 이미지 파일에 대한 키네임 생성
            String originalName = file.getOriginalFilename();

            // S3에 업로드
            try {
                amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), originalName, file.getInputStream(), objectMetadata));
                // 업로드된 이미지 url 저장
                String imageUrl = amazonS3.getUrl(amazonConfig.getBucket(), originalName).toString();
                imageUrlList.add(imageUrl);
            } catch (IOException e) { // 업로드 실패
                log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
            }
        }
        return imageUrlList;
    }

}