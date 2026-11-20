package com.diaconn_mall.website.batch.settlement;

import java.time.LocalDateTime;

/** 정산 대상 주문 한 건 (결제 완료 주문 + 결제 금액·결제일). */
public record OrderPayRow(Long orderId, Long totalPrice, Long payAmount, LocalDateTime payDate) {
}
