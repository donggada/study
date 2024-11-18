package com.side.mvcTraffic.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @Schema(description = "사용자명", example = "Tester")
    private String username;

    @Schema(description = "사용자의 로그인 아이디", example = "test123")
    private String loginId;

    @Schema(description = "사용자의 비밀번호", example = "password123")
    private String password;
}
