package com.diaconn_mall.website.batch.settlement;

/** 주문 상세 한 줄. product_id·수량·금액은 DB 에서 NULL 이 들어올 수 있어 래퍼 타입으로 둔다. */
public record OrderLine(Long orderId, Long productId, Long price, Long quantity, Long lineTotal) {
}
