package com.side.mvcTraffic.domain.coupon.repository.mysql;


import com.side.mvcTraffic.domain.coupon.entity.Coupon;
import com.side.mvcTraffic.global.config.redis.CacheConfig;
import jakarta.persistence.LockModeType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.side.mvcTraffic.global.config.redis.CacheConfig.CACHE;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findCouponWithLock(long id);

    @Cacheable(value = CACHE, key = "'coupon:' + #id", unless = "#result == null")
    Optional<Coupon> findCacheCouponById(long id);
//    Coupon findCacheCouponById(long id);
}
