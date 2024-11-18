package com.side.mvcTraffic.domain.user.dto.response;

public record LoginResponse(String loginId, String token) {

    public static LoginResponse of (String loginId, String token) {
        return new LoginResponse(loginId, token);
    }
}
