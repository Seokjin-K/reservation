package com.reservation.auth.signin;

import com.reservation.entity.user.UserEntity;
import com.reservation.entity.user.UserRole;
import lombok.Builder;

@Builder
public class SignInResponse {
    private Long id;
    private String account;
    private String password;
    private String name;
    private UserRole userRole;

    public static SignInResponse from(UserEntity user){
        return SignInResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())
                .name(user.getName())
                .userRole(user.getUserRole())
                .build();
    }
}
