package com.reservation.exception.extend;

import com.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotValidReservationException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "유효하지 않은 예약입니다.";
    }
}
