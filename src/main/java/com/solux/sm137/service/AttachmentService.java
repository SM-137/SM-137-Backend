package com.solux.sm137.service;

import com.solux.sm137.domain.*;
import com.solux.sm137.dto.request.*;
import com.solux.sm137.dto.response.*;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    // 파일 저장 로직
    public void saveAttachment(Complaint complaint, MultipartFile file) {
        // 파일 이름과 저장 경로
        String fileName = generateUniqueFileName(file.getOriginalFilename()); // 파일 이름 중복 방지

        // 파일 저장 경로
        String uploadDir = "uploads";  // 상대 경로를 사용할 경우, 프로젝트 루트 내 업로드 폴더
        String realUploadDir = getRealUploadDir(uploadDir);
        File dir = new File(realUploadDir);

        // 디렉터리가 존재하지 않으면 생성
        if (!dir.exists()) {
            dir.mkdirs();  // 폴더가 없다면 생성
        }

        String uploadPath = realUploadDir + File.separator + fileName;
        File destination = new File(uploadPath);

        try {
            // 파일을 지정된 경로로 저장
            file.transferTo(destination);

            // 첨부파일 엔티티 생성
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setUploadPath(uploadPath);
            attachment.setComplaint(complaint);

            // `modified_name` 값 설정
            attachment.setModifiedName(fileName); // 이 부분 추가

            // 첨부파일 정보를 DB에 저장
            attachmentRepository.save(attachment);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }

    // 파일명 중복 방지를 위한 고유 파일명 생성
    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        return uniqueFileName;
    }

    // 배포 환경에 맞는 실제 업로드 디렉터리 경로를 반환
    private String getRealUploadDir(String uploadDir) {
        String realPath = uploadDir;
        // 배포 환경에서는 절대 경로로 수정
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows 환경에서 절대 경로 지정 (예시)
            realPath = "C:/uploads";
        } else {
            // Linux/Mac 환경에서는 예시로 /var/www/uploads를 사용할 수 있음
            realPath = "/var/www/uploads";
        }
        return realPath;
    }

    // 특정 민원의 첨부파일 목록 조회
    public List<Attachment> getAttachmentsByComplaint(Complaint complaint) {
        return attachmentRepository.findByComplaint(complaint);
    }

    // 첨부파일 삭제 로직
    public void deleteAttachment(Attachment attachment) {
        // 파일 시스템에서 파일 삭제
        File file = new File(attachment.getUploadPath());
        if (file.exists() && file.delete()) {
            System.out.println("File deleted: " + attachment.getUploadPath());
        } else {
            System.out.println("File not found or could not be deleted: " + attachment.getUploadPath());
        }

        // DB에서 첨부파일 삭제
        attachmentRepository.delete(attachment);
    }
}
