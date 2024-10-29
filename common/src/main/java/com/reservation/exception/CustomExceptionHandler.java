package com.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    // HTTP Request -> filter(JWT 등) -> DispatcherServlet -> Controller ->
    // Service(예외 발생) -> @ExceptionHandle -> HTTP Response
    // 서비스에서 지정된 에러가 발생하면 해당 에러를 잡아서 Response 로 던질 수 있다.
    // 해당 메서드에서 에러가 발생했을 때 이 에러를 잡아서 어떻게 던질지 정해준다.

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(
            AbstractException e) {

        ErrorResponse errorResponse = ErrorResponse.of(e);
        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));
    }
}
