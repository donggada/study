package com.side.mvcTraffic.domain.user.controller;



import com.side.mvcTraffic.domain.user.dto.request.LoginRequest;
import com.side.mvcTraffic.domain.user.dto.request.UserRegisterRequest;
import com.side.mvcTraffic.domain.user.dto.response.LoginResponse;
import com.side.mvcTraffic.domain.user.dto.response.RegisterResponse;
import com.side.mvcTraffic.domain.user.dto.response.UserResponse;
import com.side.mvcTraffic.domain.user.service.UserService;
import com.side.mvcTraffic.global.config.security.CustomUserDetails;
import com.side.mvcTraffic.global.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    @Operation(summary = "사용자 로그인", description = "사용자가 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ServerExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ServerExceptionResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    @PostMapping("register")
    public RegisterResponse register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }


    @Operation(summary = "사용자가 신청한 챌린지 조회", description = "사용자가 신청한 챌린지 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping()
    public UserResponse findUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.findUser(userDetails.getUser());

    }


}
