package com.reservation.controller;

import com.reservation.auth.signin.SignInRequest;
import com.reservation.auth.signin.SignInResponse;
import com.reservation.auth.signup.SignUpRequest;
import com.reservation.auth.signup.SignUpResponse;
import com.reservation.service.SignUpService;
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

    private final SignUpService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            @Valid @RequestBody SignUpRequest request) {

        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(
            @Valid @RequestBody SignInRequest request){

        return ResponseEntity.ok();
    }
}
