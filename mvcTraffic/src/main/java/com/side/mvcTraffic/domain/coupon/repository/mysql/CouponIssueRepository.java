package com.side.mvcTraffic.domain.coupon.repository.mysql;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.mvcTraffic.domain.coupon.entity.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.side.mvcTraffic.domain.coupon.entity.QCouponIssue.couponIssue;


@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private final JPAQueryFactory queryFactory;

    public CouponIssue findFirstCouponIssue(long couponId, long userId) {
        return queryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId))
                .where(couponIssue.userId.eq(userId))
                .fetchFirst();
    }
}
