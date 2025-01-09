package com.solux.sm137.service;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.ComplaintLike;
import com.solux.sm137.domain.User;
import com.solux.sm137.repository.ComplaintLikeRepository;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComplaintLikeService {

    private final ComplaintLikeRepository complaintLikeRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 민원 좋아요 추가
    // 민원 좋아요 추가
    public ApiResponse<Void> addComplaintLike(String token, Long complaintId) {
        try {
            // JWT 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("인증되지 않은 유저입니다."));

            // 민원 조회
            Complaint complaint = complaintRepository
                    .findById(complaintId).orElseThrow(() -> new IllegalArgumentException("민원이 없습니다."));

            // 이미 좋아요가 있는지 확인
            boolean exists = complaintLikeRepository.existsByUserIdAndComplaintId(user.getId(), complaintId);
            if (exists) {

                return ApiResponse.onFailure(null, FailureStatus._ALREADY_LIKED_COMPLAINT);
            }

            // 좋아요 저장
            ComplaintLike complaintLike = ComplaintLike.builder()
                    .user(user)
                    .complaint(complaint)
                    .build();
            complaintLikeRepository.save(complaintLike);

            // 성공 응답 반환
            return ApiResponse.onSuccess(null, SuccessStatus._POST_COMPLAINT_LIKE_SUCCESS);

        } catch (IllegalArgumentException e) {
            // 실패 응답 반환
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }


    // 민원 좋아요 삭제
    public ApiResponse<Void> removeComplaintLike(String token, Long complaintId) {
        try {
            // JWT 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("인증되지 않은 유저입니다."));

            // 민원 조회
            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new IllegalArgumentException("민원이 없습니다."));

            // 좋아요 존재 여부 확인
            ComplaintLike complaintLike = complaintLikeRepository.findByUserIdAndComplaintId(user.getId(), complaintId)
                    .orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));

            // 좋아요 삭제
            complaintLikeRepository.delete(complaintLike);

            // 성공 응답 반환
            return ApiResponse.onSuccess(null, SuccessStatus._DELETE_COMPLAINT_LIKE_SUCCESS);

        } catch (Exception e) {
            // 실패 응답 반환
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }
}
