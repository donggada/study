package com.side.webFluxTraffic.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ ApplicationException.class })
    protected ResponseEntity handleApiException(ApplicationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ServerExceptionResponse(ex.getCode(), ex.getReason()));
    }

//    @ExceptionHandler({ UsernameNotFoundException.class })
//    protected ResponseEntity UsernameNotFoundException(UsernameNotFoundException ex) {
//        return ResponseEntity.status(UNAUTHORIZED).body(new ServerExceptionResponse(UNAUTHORIZED_ACCESS.getCode(), "UsernameNotFoundException -> " + ex.getMessage()));
//    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity exception(Exception ex) {
        log.error("Error message = {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ServerExceptionResponse(INTERNAL_SERVER_ERROR.toString(), ex.getMessage()));
    }

    public record ServerExceptionResponse(String code, String reason) {
    }

}
