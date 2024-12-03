package com.side.mvcTraffic.domain.coupon.factory;

import com.side.mvcTraffic.domain.coupon.service.CouponIssueDBLockServiceImp;
import com.side.mvcTraffic.domain.coupon.service.CouponIssueRedisService;
import com.side.mvcTraffic.domain.coupon.service.CouponIssueServiceImp;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class CouponIssueFactory {

    private final Map<Class, CouponIssueService> couponIssueServiceMap;

    public CouponIssueFactory(List<CouponIssueService> couponIssueServices) {
        this.couponIssueServiceMap = couponIssueServices.stream()
                .collect(
                        toMap(
                                AopUtils::getTargetClass,
                                service -> service
                        )
                );
    }

    public CouponIssueService getService(CouponIssueServiceType type) {
        switch (type) {
            case ASYNC:
                return couponIssueServiceMap.get(CouponIssueServiceImp.class);
            case DB_LOCK:
                return couponIssueServiceMap.get(CouponIssueDBLockServiceImp.class);
            case REDIS:
                return couponIssueServiceMap.get(CouponIssueRedisService.class);
            default:
                throw new IllegalArgumentException("Unsupported mall type: " + type);
        }
    }
}
