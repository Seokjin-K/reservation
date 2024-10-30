package com.reservation.service.auth;

import com.reservation.auth.signin.SignInRequest;
import com.reservation.entity.user.UserEntity;
import com.reservation.exception.extend.MismatchPasswordException;
import com.reservation.exception.extend.NonExistAccountException;
import com.reservation.jwt.JwtTokenProvider;
import com.reservation.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignInService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 메서드
     * 1. 계정이 존재하는지 체크
     * 2. 비밀번호가 일치하는지 체크
     * 3. 토큰 생성하여 반환
     *
     * @param request
     * @return
     */
    public String login(SignInRequest request) {
        UserEntity userEntity = this.userRepository.findByAccount(
                request.getAccount()).orElseThrow(NonExistAccountException::new);

        if (!this.passwordEncoder.matches(
                request.getPassword(), userEntity.getPassword())
        ) {
            throw new MismatchPasswordException();
        }

        String token = this.jwtTokenProvider.generateToken(
                userEntity.getAccount(), userEntity.getUserRole());

        log.info("\u001B[32muser login -> {}", userEntity.getAccount() +
                "\u001B[0m");

        return token;
    }
}
