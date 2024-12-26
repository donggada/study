package com.side.mvcTraffic.domain.coupon.controller;

import com.side.mvcTraffic.domain.coupon.dto.request.CouponIssueRequest;
import com.side.mvcTraffic.domain.coupon.dto.response.CouponIssueResponse;
import com.side.mvcTraffic.domain.coupon.factory.CouponIssueFactory;
import com.side.mvcTraffic.domain.coupon.factory.CouponIssueServiceType;
import com.side.mvcTraffic.domain.coupon.service.CouponIssueDBLockServiceImp;
import com.side.mvcTraffic.domain.coupon.service.CouponIssueRedisService;
import com.side.mvcTraffic.domain.coupon.factory.CouponIssueService;
import com.side.mvcTraffic.global.component.DistributeLockExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.side.mvcTraffic.domain.coupon.factory.CouponIssueServiceType.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CouponController {

    private final CouponIssueFactory couponIssueFactory;
    private final DistributeLockExecutor distributeLockExecutor = null;

    @PostMapping("/v1/coupon")
    public CouponIssueResponse issueV1 (@RequestBody CouponIssueRequest request) {
//        동시성 제어 해결 방법
//        1.synchronized 적용 -> 문제점 다중서버서 일때 동시성 제어 X , 성능이슈
        synchronized (this) {
            couponIssueFactory.getService(ASYNC).issue(request.couponId(), request.userId());
        }

//        2.레디스 락 (분산락) 적용 -> 문제점 성능이슈
        distributeLockExecutor.execute("lock_%s".formatted(request.userId()), 3000, 3000, () -> {
            couponIssueFactory.getService(ASYNC).issue(request.couponId(), request.userId());
        });

//      3.DB 락 적용 @Lock(LockModeType.PESSIMISTIC_WRITE) 적용 -> DB CPU 증가
        //todo 추상화 하기
        couponIssueFactory.getService(DB_LOCK).issue(request.couponId(), request.userId());
        return CouponIssueResponse.successOf();
    }

    @PostMapping("/v2/coupon")
    public CouponIssueResponse issueV2 (@RequestBody CouponIssueRequest request) {
        couponIssueFactory.getService(REDIS).issue(request.couponId(), request.userId());
        return CouponIssueResponse.successOf();
    }
}
