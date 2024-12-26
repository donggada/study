package com.side.mvcTraffic.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.side.mvcTraffic.global.exception.ErrorCode.UNAUTHORIZED_ACCESS;


@Component
public class CustomAuthenticationException implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<?> responseEntity = new ResponseEntity<>(new GlobalExceptionHandler.ServerExceptionResponse(UNAUTHORIZED_ACCESS.getCode(), UNAUTHORIZED_ACCESS.getMessage(), UNAUTHORIZED_ACCESS.getReason()), HttpStatus.UNAUTHORIZED);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
}

