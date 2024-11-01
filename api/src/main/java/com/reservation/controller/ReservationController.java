package com.reservation.controller;

import com.reservation.reservation.ReservationRequest;
import com.reservation.reservation.ReservationResponse;
import com.reservation.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 생성
     * 로그인된 userId와 request 로 예약 생성 시도
     *
     * @param userId
     * @param request
     * @return
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
}
