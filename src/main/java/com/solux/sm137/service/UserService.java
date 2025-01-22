package com.solux.sm137.service;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.ComplaintStatus;
import com.solux.sm137.domain.Scrap;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.ModifyUserRequest;
import com.solux.sm137.dto.response.MyComplaintResponse;
import com.solux.sm137.dto.response.ResultResponse;
import com.solux.sm137.dto.response.ScrapResponse;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.ScrapRepository;
import com.solux.sm137.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final ScrapRepository scrapRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void modifyUser(String token, ModifyUserRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        // 없으면 에러 날림
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        user.modifyUser(request.getNumber(), request.getDepartment());
        userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        // 없으면 에러 날림
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        return new UserInfoResponse(user.getName(), user.getEmail(), user.getNumber(), user.getDepartment());
    }

    @Transactional(readOnly = true)
    public List<MyComplaintResponse> getMyComplaints(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        // 없으면 에러 날림
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 목록 가져오기
        List<Complaint> complaints = complaintRepository.findByUserOrderByCreatedAtDesc(user).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 목록을 MyComplaintResponse로 변환
        return complaints.stream()
                .map(complaint -> new MyComplaintResponse(
                        complaint.getId(),
                        complaint.getTitle(),
                        complaint.getStatus(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size(),
                        complaint.getCategory().getCategoryName(),
                        complaint.getTag().getTagName(),
                        complaint.getCreatedAt()))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<ResultResponse> getResults(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        // 없으면 에러 날림
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 목록 가져오기
        List<Complaint> complaints = complaintRepository.findByUserOrderByCreatedAtDesc(user).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 목록을 MyComplaintResponse로 변환
        return complaints.stream()
                .filter(complaint -> complaint.getStatus() == ComplaintStatus.DONE)
                .map(complaint -> new ResultResponse(
                        complaint.getId(),
                        complaint.getTitle(),
                        complaint.getStatus(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size(),
                        complaint.getCategory().getCategoryName(),
                        complaint.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScrapResponse> getScraps(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 스크랩 목록 가져오기
        List<Scrap> scraps = scrapRepository.findByUserOrderByCreatedAtDesc(user).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 스크랩 목록을 ScrapResponse로 변환
        return scraps.stream()
                .map(scrap -> {
                    Complaint complaint = scrap.getComplaint();
                    return new ScrapResponse(
                            complaint.getId(),
                            complaint.getTitle(),
                            complaint.getStatus(),
                            complaint.getContentProb(),
                            complaint.getComplaintLikes().size(),
                            complaint.getScraps().size(),
                            complaint.getCategory().getCategoryName(),
                            complaint.getTag().getTagName(),
                            complaint.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public void deleteUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
