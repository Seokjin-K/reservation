package com.reservation.controller;

import com.reservation.entity.user.UserEntity;
import com.reservation.reservation.ReservationRequest;
import com.reservation.reservation.ReservationResponse;
import com.reservation.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 생성
     * 로그인된 userId와 request 로 예약 생성 시도
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.ok(this.reservationService.createReservation(
                userId, request
        ));
    }

    /**
     * 파트너 회원이 가능한 예약 상태 변경
     * APPROVE, REJECTED, CANCELED, VISITED, NO_SHOW
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PatchMapping("/{reservationId}/{status}")
    public ResponseEntity<ReservationResponse> patchReservation(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long reservationId,
            @PathVariable String status
    ) {
        return ResponseEntity.ok(
                this.reservationService.patchReservationCustomer(
                        userId, reservationId, status));
    }

    /**
     * 일반 회원이 가능한 예약 상태 변경
     * CANCELED
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{reservationId}/customer/cancel")
    public ResponseEntity<ReservationResponse> patchReservation(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(
                this.reservationService.patchReservationCustomer(
                        userId, reservationId));
    }

    /**
     * 일반 회원의 본인이 한 모든 예약 read
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservation(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(
                this.reservationService.getReservation(userId));
    }


    /**
     * 파트너 회원의 해당 매장의 모든 예약 read
     */
    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("{storeId}")
    public ResponseEntity<List<ReservationResponse>> getReservation(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(
                this.reservationService.getReservation(userEntity, storeId));
    }

    /**
     * 예약 10분전에 도착하여 방문 확인 진행
     */
    @PatchMapping("/check/in/{storeId}/{reservationId}")
    public ResponseEntity<ReservationResponse> checkIn(
            @PathVariable Long storeId,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(
                this.reservationService.checkIn(storeId, reservationId));
    }
}
