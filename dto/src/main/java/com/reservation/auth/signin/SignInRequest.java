package com.reservation.auth.signin;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SignInRequest {

    @NotBlank(message = "계정은 필수 입력값입니다.")
    private String account;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
