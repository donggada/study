package com.side.mvcTraffic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //BAD_REQUEST 잘못된 요청
    INVALID_LOGIN_ID(CONFLICT,BAD_REQUEST.toString(), "아이디 확인해주세요."),
    INVALID_PASSWORD(CONFLICT, BAD_REQUEST.toString(),"비번 확인해주세요."),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED, UNAUTHORIZED.toString(),"인증되지 않은 접근 입니다."),
    DUPLICATE_LOGIN_ID(CONFLICT, BAD_REQUEST.toString(), "이미 존재한 아이디 입니다."),
    USER_NOT_FOUND(NOT_FOUND, NOT_FOUND.toString(), "유저정보를 찾을수 없습니다."),

    QUEUE_ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "UQ-0001", "이미 대기열이 있습니다."),

    INVALID_COUPON_ISSUE_QUANTITY(CONFLICT, BAD_REQUEST.toString(),"쿠폰 발급 수량이 유효하지 않습니다."),
    INVALID_COUPON_ISSUE_DATE(CONFLICT, BAD_REQUEST.toString(),"쿠폰 발급 기간이 유효하지 않습니다."),
    COUPON_NOT_EXIST(CONFLICT, BAD_REQUEST.toString(),"존재하지 않는 쿠폰입니다."),
    DUPLICATED_COUPON_ISSUE(CONFLICT, BAD_REQUEST.toString(),"이미 발급된 쿠폰입니다."),
    FAIL_COUPON_ISSUE_REQUEST(CONFLICT, BAD_REQUEST.toString(),"쿠폰 발급 요청에 실패했습니다"),


    //500 INTERNAL SERVER ERROR
    SERVER_ERROR(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.toString(),"서버 에러입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;

    public ApplicationException build() {
        return new ApplicationException(httpStatus, code, reason);
    }

    public CouponIssueException couponIssueBuild() {
        return new CouponIssueException(httpStatus, code, reason);
    }

    public ApplicationException build(Object ...args) {
        return new ApplicationException(httpStatus, code, reason.formatted(args));
    }
}
