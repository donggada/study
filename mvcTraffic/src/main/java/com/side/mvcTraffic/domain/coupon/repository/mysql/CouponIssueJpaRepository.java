package com.side.mvcTraffic.domain.coupon.repository.mysql;


import com.side.mvcTraffic.domain.coupon.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
