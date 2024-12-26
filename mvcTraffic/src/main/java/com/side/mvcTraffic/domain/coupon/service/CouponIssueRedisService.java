package com.side.mvcTraffic.domain.coupon.service;

import com.side.mvcTraffic.domain.coupon.entity.Coupon;
import com.side.mvcTraffic.domain.coupon.factory.CouponIssueService;
import com.side.mvcTraffic.domain.coupon.repository.mysql.CouponJpaRepository;
import com.side.mvcTraffic.domain.coupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.side.mvcTraffic.global.exception.ErrorCode.COUPON_NOT_EXIST;

@Service("couponIssueRedisService")
@RequiredArgsConstructor
public class CouponIssueRedisService implements CouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponJpaRepository couponJpaRepository;


    @Override
    @Transactional
    public void issue(long couponId, long userId) {
        /*
        todo
        Coupon 엔티티 cache 처리 필요.
        cache 할때 availableIssueDate 같이 처리하는게 필요하라?
         */
//        Coupon coupon = couponJpaRepository.findCacheCouponById(couponId);
        Coupon coupon = couponJpaRepository.findCacheCouponById(couponId).orElseThrow(() -> COUPON_NOT_EXIST.build(couponId));
        coupon.availableDate();
//        issueRequest(couponId, userId, coupon.getTotalQuantity());
    }

    // RedisScript 사용해서 동시성 제어 및 성능개선
    // 레디스 싱글 쓰레드 이기 떄문에 하나에 동시성이 일어나지 않는다.
    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity) {
        if(totalIssueQuantity == null) {
            redisRepository.issueRequest(couponId, userId, Integer.MAX_VALUE);
            return;
        }
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }
}
