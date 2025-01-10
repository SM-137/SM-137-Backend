package com.solux.sm137.service;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.CommentRequest;
import com.solux.sm137.dto.response.CommentResponse;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.infra.exception.ComplaintNotFoundException;
import com.solux.sm137.infra.exception.UserNotFoundException;
import com.solux.sm137.repository.CommentRepository;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 댓글 작성
    public Comment createComment(String token, Long complaintId, CommentRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        // 댓글 저장
        Comment comment = new Comment();
        comment.setComplaint(complaint);
        comment.setUser(user);
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    // 댓글 목록 조회
    public List<CommentResponse> getComments(Long complaintId, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        // 없으면 에러 날림
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        List<Comment> comments = commentRepository.findByComplaintId(complaintId);

        return comments.stream().map(comment -> {
                    boolean isLiked = comment.getCommentLikes().stream()
                            .anyMatch(like -> like.getUser().getId().equals(user.getId()));
                    return new CommentResponse(
                            comment.getId(),
                            comment.getUser().getId(),
                            comment.getUser().getEmail(),
                            comment.getContent(),
                            isLiked,
                            comment.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }
}
