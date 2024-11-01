package com.reservation.reservation;

import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.reservation.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private String name;
    private Integer numberOfPeople;
    private LocalDateTime reservationTime;
    private ReservationStatus reservationStatus;

    public static ReservationResponse from(ReservationEntity reservationEntity) {
        return ReservationResponse.builder()
                .id(reservationEntity.getId())
                .name(reservationEntity.getName())
                .numberOfPeople(reservationEntity.getNumberOfPeople())
                .reservationTime(reservationEntity.getReservationTime())
                .reservationStatus(reservationEntity.getReservationStatus())
                .build();
    }
}
