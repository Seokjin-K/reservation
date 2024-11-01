package com.reservation.entity.reservation;

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
    NO_SHOW
}
