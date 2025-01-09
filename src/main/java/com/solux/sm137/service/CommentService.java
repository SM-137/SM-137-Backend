package com.solux.sm137.service;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.CommentRequest;
import com.solux.sm137.repository.CommentRepository;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.infra.exception.UserNotFoundException;
import com.solux.sm137.infra.exception.ComplaintNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 댓글 작성
    public Comment createComment(String token, Long complaintId, CommentRequest request) {
        // JWT 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 사용자 조회
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        // 민원 조회
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        // 댓글 저장
        Comment comment = new Comment();
        comment.setComplaint(complaint);
        comment.setUser(user);
        comment.setContent(request.getContent());

        return commentRepository.save(comment);
    }

    // 댓글 목록 조회
    public List<Comment> getComments(Long complaintId) {
        // 해당 민원에 달린 댓글 조회
        return commentRepository.findByComplaintId(complaintId);
    }
}
