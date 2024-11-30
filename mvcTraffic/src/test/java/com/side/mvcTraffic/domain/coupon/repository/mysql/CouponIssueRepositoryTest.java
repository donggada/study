package com.side.mvcTraffic.domain.coupon.repository.mysql;

import com.side.mvcTraffic.domain.coupon.entity.CouponIssue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("local")
class CouponIssueRepositoryTest {

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Test
    void test () {
        CouponIssue couponIssueResult = couponIssueRepository.findFirstCouponIssue(1, 1);
    }

}