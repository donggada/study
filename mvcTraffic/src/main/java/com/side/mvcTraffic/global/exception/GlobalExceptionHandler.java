package com.side.mvcTraffic.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.side.mvcTraffic.global.exception.ErrorCode.UNAUTHORIZED_ACCESS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ ApplicationException.class })
    protected ResponseEntity handleApiException(ApplicationException ex) {
        log.error("error message = {} , reason = {}", ex.getMessage(), ex.getReason(), ex);
        return ResponseEntity.status(ex.getHttpStatus()).body(new ServerExceptionResponse(ex.getCode(), ex.getMessage(), ex.getReason()));
    }

    @ExceptionHandler({ UsernameNotFoundException.class })
    protected ResponseEntity UsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ServerExceptionResponse(UNAUTHORIZED_ACCESS.getCode(), "인증 에러 입니다.","UsernameNotFoundException -> " + ex.getMessage()));
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity exception(Exception ex) {
        log.error("error message = {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ServerExceptionResponse(INTERNAL_SERVER_ERROR.toString(), "서버 에러입니다.", ex.getMessage()));
    }

    public record ServerExceptionResponse(String code, String message, String reason) {
    }

}
