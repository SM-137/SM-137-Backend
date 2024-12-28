package com.solux.sm137.service;

import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.ModifyUserRequest;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void modifyUser(String token, ModifyUserRequest request) {
        if(token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        // 없으면 에러 날림
        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        user.modifyUser(user.getNumber(), user.getDepartment());

    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String token) {
        if(token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        // 없으면 에러 날림
        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        return new UserInfoResponse(user.getName(), user.getEmail(), user.getNumber(), user.getDepartment());
    }
}
