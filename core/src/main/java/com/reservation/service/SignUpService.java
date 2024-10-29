package com.reservation.service;

import com.reservation.auth.signup.SignUpRequest;
import com.reservation.auth.signup.SignUpResponse;
import com.reservation.exception.extend.AlreadyExistAccountException;
import com.reservation.repository.user.UserRepository;
import com.reservation.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 메서드
     * 1. 계정 중복 체크
     * 2. userEntity 저장
     * @param request
     * @return
     */
    public SignUpResponse register(SignUpRequest request) {
        String account = request.getAccount();

        checkDuplicateAccount(account); // 계정 중복 체크
        UserEntity userEntity = buildUserEntity(request);

        this.userRepository.save(userEntity);
        return SignUpResponse.from(userEntity);
    }

    private void checkDuplicateAccount(String account) {
        this.userRepository.existsByAccount(account);

        if (this.userRepository.existsByAccount(account)) {
            throw new AlreadyExistAccountException();
        }
    }

    private UserEntity buildUserEntity(SignUpRequest signUpRequest) {
        return UserEntity.builder()
                .account(signUpRequest.getAccount())
                .password(this.passwordEncoder
                        .encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .userRole(signUpRequest.getUserRole())
                .build();
    }
}
