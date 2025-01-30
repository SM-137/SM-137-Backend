package com.solux.sm137.service;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.CommentLike;
import com.solux.sm137.domain.User;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.repository.CommentLikeRepository;
import com.solux.sm137.repository.CommentRepository;
import com.solux.sm137.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 댓글 좋아요 추가
    public ApiResponse<Void> addCommentLike(String token, Long commentId) {
        try {
            // JWT 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("인증되지 않은 유저입니다."));

            // 댓글 조회
            Comment comment = commentRepository
                    .findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));

            // 이미 좋아요가 있는지 확인
            boolean exists = commentLikeRepository.existsByUserAndComment(user, comment);
            if (exists) {
                return ApiResponse.onFailure(null, FailureStatus._ALREADY_LIKED_COMMENT);
            }

            // 좋아요 저장
            CommentLike commentLike = new CommentLike();
            commentLike.setUser(user);
            commentLike.setComment(comment);
            commentLikeRepository.save(commentLike);

            // 성공 응답 반환
            return ApiResponse.onSuccess(null, SuccessStatus._POST_COMMENT_LIKE_SUCCESS);

        } catch (Exception e) {
            // 실패 응답 반환
            e.printStackTrace();  // 예외의 내용도 출력해보세요.
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }


    // 댓글 좋아요 삭제
    public ApiResponse<Void> removeCommentLike(String token, Long commentId) {
        try {
            // JWT 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 사용자 조회
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // 댓글 조회
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));

            // 좋아요 존재 여부 확인
            CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                    .orElseThrow(() -> new IllegalArgumentException("CommentLike not found"));

            // 좋아요 삭제
            commentLikeRepository.delete(commentLike);

            // 성공 응답 반환
            return ApiResponse.onSuccess(null, SuccessStatus._DELETE_COMMENT_LIKE_SUCCESS);

        } catch (Exception e) {
            // 실패 응답 반환
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }
}
