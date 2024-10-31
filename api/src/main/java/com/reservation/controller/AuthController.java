package com.reservation.controller;

import com.reservation.auth.signin.SignInRequest;
import com.reservation.auth.signup.SignUpRequest;
import com.reservation.auth.signup.SignUpResponse;
import com.reservation.service.auth.SignInService;
import com.reservation.service.auth.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SignUpService signUpService;
    private final SignInService signInService;

    // HTTP 요청 -> Filter -> ServletDispatcher -> 인터셉터 -> AOP -> Controller

    /**
     * 회원가입
     * @param request
     * @return 회원 정보를 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(this.signUpService.register(request));
    }

    /**
     * 로그인
     * @return 해당 계정의 토큰을 반환
     */
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(
            @Valid @RequestBody SignInRequest request){

        return ResponseEntity.ok(this.signInService.login(request));
    }
}
