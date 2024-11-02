package com.reservation.exception.extend;

import com.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class VisitTooEarlyException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "너무 일찍 방문하셨습니다. 10분 전에 방문해 주세요.";
    }
}
