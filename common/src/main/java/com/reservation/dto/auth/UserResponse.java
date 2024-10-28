package com.reservation.dto.auth;

import com.reservation.constant.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String account;
    private String password;
    private String name;
    private UserRole userRole;

    public static UserResponse of(
            Long id, String account, String password, String name,
            UserRole userRole) {
        return new UserResponse(id, account, password, name, userRole);
    }
}
