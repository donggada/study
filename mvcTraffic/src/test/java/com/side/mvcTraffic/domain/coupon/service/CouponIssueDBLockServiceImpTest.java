package com.side.mvcTraffic.domain.coupon.service;


import com.side.mvcTraffic.domain.coupon.entity.Coupon;
import com.side.mvcTraffic.domain.coupon.entity.CouponType;
import com.side.mvcTraffic.domain.coupon.repository.mysql.CouponJpaRepository;
import com.side.mvcTraffic.global.exception.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static com.side.mvcTraffic.global.exception.ErrorCode.*;

@SpringBootTest
@ActiveProfiles("local")
class CouponIssueDBLockServiceImpTest {

    @Autowired
    CouponIssueDBLockServiceImp couponIssueDBLockServiceImp;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issue_1() {
        long couponId = 1;
        long userId = 1;

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            couponIssueDBLockServiceImp.issue(couponId, userId);
        });

        Assertions.assertEquals(exception.getMessage(), COUPON_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issue_2() {
        // given
        long userId = 1000;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(10)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        Coupon saveCoupon = couponJpaRepository.save(coupon);

        // when & then
        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            couponIssueDBLockServiceImp.issue(saveCoupon.getId(), userId);
        });
        Assertions.assertEquals(exception.getMessage(), INVALID_COUPON_ISSUE_QUANTITY.getMessage());
    }


    @Test
    @DisplayName("쿠폰 발급 - 발급 기한이 유효하지 않다면 예외를 반환한다")
    void issue_3() {

        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        Coupon saveCoupon = couponJpaRepository.save(coupon);

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            couponIssueDBLockServiceImp.issue(saveCoupon.getId(), userId);
        });

        Assertions.assertEquals(exception.getMessage(), INVALID_COUPON_ISSUE_DATE.getMessage());
    }

}