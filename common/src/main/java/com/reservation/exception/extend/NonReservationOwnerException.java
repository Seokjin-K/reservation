package com.reservation.exception.extend;

import com.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NonReservationOwnerException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "해당 유저의 예약이 아닙니다.";
    }
}
