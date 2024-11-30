package com.side.mvcTraffic.domain.coupon.service;

public interface CouponIssueService {

    void issue(long couponId, long userId);

    void issueRequest(long couponId, long userId);
}
