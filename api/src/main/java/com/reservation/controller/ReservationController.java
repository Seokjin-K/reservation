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
     * 로그인된 유저의 엔티티와 요청된 예약 정보로 예약 생성
     *
     * @param userEntity 로그인된 유저의 엔티티
     * @param request    요청된 예약 정보
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal UserEntity userEntity,
            @Valid @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.ok(this.reservationService.createReservation(
                userEntity, request
        ));
    }

    /**
     * 파트너 회원이 가능한 예약 상태 변경
     * APPROVE, REJECTED, CANCELED, VISITED, NO_SHOW
     *
     * @param userId        예약을 받은 매장의 점주 유저 id
     * @param reservationId 예약 상태를 변경할 예약의 id
     * @param status        변경할 상태
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PatchMapping("/partner/{reservationId}/{status}")
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
     *
     * @param userId 예약 상태를 바꾸려는 회원의 id
     * @param reservationId 예약 상태를 바꾸려는 예약의 id
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/customer/{reservationId}")
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
     *
     * @param userId 로그인된 회원의 유저의 id
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer")
    public ResponseEntity<List<ReservationResponse>> getReservation(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(
                this.reservationService.getReservation(userId));
    }


    /**
     * 파트너 회원의 해당 매장의 모든 예약 read
     *
     * @param userEntity 로그인된 유저의 엔티티
     * @param storeId    예약을 확인하려는 매장의 id
     */
    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("/partner/{storeId}")
    public ResponseEntity<List<ReservationResponse>> getReservation(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(
                this.reservationService.getReservation(userEntity, storeId));
    }

    /**
     * 예약 10분전에 도착하여 방문 확인 진행
     *
     * @param storeId 방문한 매장의 id
     * @param reservationId 확인할 예약의 id
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
