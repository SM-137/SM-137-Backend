package com.solux.sm137.service;

import com.solux.sm137.domain.User;
import com.solux.sm137.dto.ModifyUserRequest;
import com.solux.sm137.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void modifyUser(String token, ModifyUserRequest modifyUserRequest) {
        if(token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        User user = userRepository.findById(1L).get();

    }

}
