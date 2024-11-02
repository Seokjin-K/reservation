package com.reservation.exception.extend;

import com.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidReviewRatingException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "리뷰의 평점은 0.5점 단위어야 합니다.";
    }
}
