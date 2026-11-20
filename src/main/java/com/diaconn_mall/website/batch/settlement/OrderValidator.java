package com.diaconn_mall.website.batch.settlement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정산 전에 주문 한 건의 금액이 서로 맞는지 확인한다.
 * 주문 총액은 클라이언트가 보낸 값을 그대로 저장하므로(OrderService), 상세 합계·결제 금액과 대조한다.
 * 규칙을 바꾸면 VerifyTasklet 의 기대값 SQL 도 같이 바꿔야 한다.
 */
public final class OrderValidator {

    public static final String NO_DETAIL = "NO_DETAIL";
    public static final String NULL_PRODUCT = "NULL_PRODUCT";
    public static final String BAD_QUANTITY = "BAD_QUANTITY";
    public static final String LINE_TOTAL_MISMATCH = "LINE_TOTAL_MISMATCH";
    public static final String TOTAL_MISMATCH = "TOTAL_MISMATCH";
    public static final String PAY_AMOUNT_MISMATCH = "PAY_AMOUNT_MISMATCH";

    private OrderValidator() {
    }

    /** 정상이면 empty, 아니면 실패 사유 코드. */
    public static Optional<String> validate(OrderPayRow order, List<OrderLine> lines) {
        if (lines == null || lines.isEmpty()) {
            return Optional.of(NO_DETAIL);
        }
        long detailSum = 0;
        for (OrderLine line : lines) {
            if (line.productId() == null) {
                return Optional.of(NULL_PRODUCT);
            }
            if (line.quantity() == null || line.quantity() <= 0) {
                return Optional.of(BAD_QUANTITY);
            }
            if (line.price() == null || line.lineTotal() == null
                    || line.price() * line.quantity() != line.lineTotal()) {
                return Optional.of(LINE_TOTAL_MISMATCH);
            }
            detailSum += line.lineTotal();
        }
        if (order.totalPrice() == null || detailSum != order.totalPrice()) {
            return Optional.of(TOTAL_MISMATCH);
        }
        if (!Objects.equals(order.payAmount(), order.totalPrice())) {
            return Optional.of(PAY_AMOUNT_MISMATCH);
        }
        return Optional.empty();
    }

    public static long detailSum(List<OrderLine> lines) {
        return lines == null ? 0 : lines.stream()
                .map(OrderLine::lineTotal).filter(Objects::nonNull).mapToLong(Long::longValue).sum();
    }
}
