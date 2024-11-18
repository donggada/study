package com.side.mvcTraffic.domain.user.dto.response;

public record RegisterResponse(String loginId, String message) {

    public static RegisterResponse of (String loginId) {
        return new RegisterResponse(loginId, "회원가입 완료");
    }
}
