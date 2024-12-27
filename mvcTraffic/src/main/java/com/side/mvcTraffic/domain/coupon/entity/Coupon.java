package com.side.mvcTraffic.domain.coupon.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.side.mvcTraffic.global.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.side.mvcTraffic.global.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;


//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    // 동시성 문제
    public boolean availableIssueQuantity() {
        if (totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;
    }

    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public void availableDate() {
        LocalDateTime now = LocalDateTime.now();
        if(!dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now)) {
            throw INVALID_COUPON_ISSUE_DATE.build("발급 가능한 일자가 아닙니다. request : %s, issueStart: %s, issueEnd: %s".formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
        }
    }


    public void issue() {
        if (!availableIssueQuantity()) {
            throw INVALID_COUPON_ISSUE_QUANTITY.build(totalQuantity, issuedQuantity);
        }
        if (!availableIssueDate()) {
            throw INVALID_COUPON_ISSUE_DATE.build(dateIssueStart, dateIssueEnd);
        }
        issuedQuantity++;
    }
}
