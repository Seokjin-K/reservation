package com.reservation.controller;

import com.reservation.dto.auth.UserRequest;
import com.reservation.dto.auth.UserResponse;
import com.reservation.model.UserEntity;
import com.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public UserResponse signUp(
            @Valid @RequestBody UserRequest request) {
        // UserEntity는 데이터베이스와 직접적으로 매핑된 엔티티로,
        // 이를 클라이언트에 그대로 노출하는 것은 보안 및 캡슐화 측면에서 좋지 않습니다.
        UserEntity userEntity = userService.register(request);
        return UserResponse.of(
                userEntity.getId(), userEntity.getAccount(),
                userEntity.getPassword(), userEntity.getName(),
                userEntity.getUserRole()
        );
    }
}
