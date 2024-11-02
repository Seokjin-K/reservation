package com.reservation.exception.extend;

import com.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidReservationStatusException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "올바른 예약 상태가 아닙니다.";
    }
}
