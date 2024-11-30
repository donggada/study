package com.side.mvcTraffic.domain.coupon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.side.mvcTraffic.global.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@RestController
public class TestController {

    @GetMapping("hello")
    public String test() {
        return "hello";
    }

    @GetMapping("test")
    public String test1 () {
        if (1==1) {
            throw INVALID_COUPON_ISSUE_QUANTITY.build(1, 2);
        }
        return "";
    }
}
