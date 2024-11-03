package com.reservation.store;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class StoreRequest {

    @NotNull(message = "매장 이름은 필수입니다.")
    private String name;

    @NotNull(message = "매장 주소는 필수입니다.")
    private String address;

    @Size(max = 1000, message = "매장 설명은 1000자를 초과할 수 없습니다.")
    private String description;
}
