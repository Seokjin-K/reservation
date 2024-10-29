package com.reservation.auth.signup;

import com.reservation.entity.user.UserEntity;
import com.reservation.entity.user.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {

    private Long id;
    private String account;
    private String password;
    private String name;
    private UserRole userRole;

   public static SignUpResponse from(UserEntity user){
        return SignUpResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())
                .name(user.getName())
                .userRole(user.getUserRole())
                .build();
    }
}
