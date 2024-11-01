package com.reservation.reservation;

import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
public class ReservationRequest {

    @NotNull(message = "매장 선택은 필수입니다.")
    private Long storeId;

    @NotEmpty(message = "예약자 이름은 필수입니다.")
    private String name;

    @NotNull(message = "인원 선택은 필수입니다.")
    @Min(value = 1, message = "예약 인원은 1명 이상이어야 합니다.")
    @Max(value = 10, message = "예약 인원은 10명 이하여야 합니다.")
    private Integer numberOfPeople;

    @NotNull(message = "예약 시간 선택은 필수입니다.")
    @Future(message = "예약 시간은 미래 시간이어야 합니다.")
    private LocalDateTime reservationTime;
}