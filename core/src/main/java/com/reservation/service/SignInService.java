package com.reservation.service;

import com.reservation.auth.signin.SignInRequest;
import com.reservation.auth.signin.SignInResponse;
import com.reservation.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final UserRepository userRepository;

    public SignInResponse login(SignInRequest request) {
        userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new )
    }
}
