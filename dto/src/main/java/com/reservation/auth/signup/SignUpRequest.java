package com.reservation.auth.signup;

import com.reservation.entity.user.UserRole;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class SignUpRequest {

    // 충족 못하면 MethodArgumentNotValidException 발생
    @NotBlank(message = "계정은 필수 입력값입니다.") // 애플리케이션 수준 검사
    @Size(max = 50, message = "계정명은 50자를 초과할 수 없습니다.")
    private String account;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(max = 255, message = "비밀번호는 255자를 초과할 수 없습니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다.")
    private String name;
    private UserRole userRole;
}
