package com.reservation.entity.reservation;

import java.util.Arrays;

/**
 * PENDING: 예약 대기 상태
 * APPROVED: 예약 승인 상태
 * REJECTED: 예약 거절 상태
 * CANCELED: 예약 취소 상태
 * VISITED: 방문 완료 상태
 * NO_SHOW: 예약 시간이 지났는데도 방문하지 않은 상태
 */
public enum ReservationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELED,
    VISITED,
    NO_SHOW;

    /**
     * 1. Enum 의 모든 값을 stream 으로 변환
     * 2. 이름이 일치하는 값 필터링(대문자로 변환)
     * 3. 첫 번째 일치하는 값 반환(Optional)
     * 4. 값이 없으면 예외 발생
     */
    public static ReservationStatus from(String status) {
        return Arrays.stream(ReservationStatus.values())
                .filter(s -> s.name().equals(status.toUpperCase()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
