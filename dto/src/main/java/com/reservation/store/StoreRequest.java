package com.reservation.store;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class StoreRequest {

    @NotBlank(message = "매장 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "매장 주소는 필수입니다.")
    private String address;

    @Size(max = 1000, message = "매장 설명은 1000자를 초과할 수 없습니다.")
    private String description;
}
