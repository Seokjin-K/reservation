package com.reservation.exception;

import lombok.Builder;

import javax.servlet.http.HttpServletRequest;

@Builder
public class ErrorResponse {

    private int code;
    private String message;

    public static ErrorResponse of(
            AbstractException e) {

        return ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }
}
