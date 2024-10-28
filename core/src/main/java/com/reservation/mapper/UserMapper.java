package com.reservation.mapper;

import com.reservation.dto.auth.UserRequest;
import com.reservation.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest user) {
        return UserEntity.builder()
                .account(user.getAccount())
                .password(user.getPassword())
                .name(user.getName())
                .userRole(user.getUserRole())
                .build();
    }
}
