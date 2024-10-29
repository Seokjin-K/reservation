package com.reservation.service;

import com.reservation.SignUpRequest;
import com.reservation.SignUpResponse;
import com.reservation.repository.user.UserRepository;
import com.reservation.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse register(SignUpRequest signUpRequest) {
        String account = signUpRequest.getAccount();

        checkDuplicateAccount(account); // 계정 중복 체크
        UserEntity userEntity = buildUserEntity(signUpRequest);

        this.userRepository.save(userEntity);
        return SignUpResponse.from(userEntity);
    }

    private void checkDuplicateAccount(String account) {
        if (this.userRepository.existsByAccount(account)) {
            throw new RuntimeException();
            // TODO: Change RuntimeException to CustomException(ErrorCode.DUPLICATE_ACCOUNT)
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
