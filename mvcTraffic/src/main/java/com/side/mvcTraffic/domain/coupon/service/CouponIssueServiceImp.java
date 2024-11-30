package com.side.mvcTraffic.domain.coupon.service;

import com.side.mvcTraffic.domain.coupon.entity.Coupon;
import com.side.mvcTraffic.domain.coupon.entity.CouponIssue;
import com.side.mvcTraffic.domain.coupon.repository.mysql.CouponIssueJpaRepository;
import com.side.mvcTraffic.domain.coupon.repository.mysql.CouponIssueRepository;
import com.side.mvcTraffic.domain.coupon.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.side.mvcTraffic.global.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.side.mvcTraffic.global.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;

@Service
@RequiredArgsConstructor
public class CouponIssueServiceImp {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;


    // 동시성 문제 발생
    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCouponWithLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }


    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() -> {
            throw COUPON_NOT_EXIST.build("쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId));
        });
    }

    @Transactional
    public Coupon findCouponWithLock(long couponId) {
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow(() -> {
           throw COUPON_NOT_EXIST.build("쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId));
        });
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId, userId);
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(couponIssue);
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        if (issue != null) {
            throw DUPLICATED_COUPON_ISSUE.build("이미 발급된 쿠폰입니다. user_id: %d, coupon_id: %d".formatted(userId, couponId));
        }
    }
}
