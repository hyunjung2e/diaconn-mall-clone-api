package com.diaconn_mall.website.batch.settlement;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderValidatorTest {

    private static final LocalDateTime PAID = LocalDateTime.of(2026, 10, 1, 12, 0);

    private static OrderPayRow order(long total, long payAmount) {
        return new OrderPayRow(1L, total, payAmount, PAID);
    }

    private static OrderLine line(Long productId, Long price, Long qty, Long lineTotal) {
        return new OrderLine(1L, productId, price, qty, lineTotal);
    }

    @Test
    void 금액이_모두_맞으면_정상() {
        List<OrderLine> lines = List.of(line(2L, 1000L, 2L, 2000L), line(3L, 500L, 1L, 500L));
        assertThat(OrderValidator.validate(order(2500, 2500), lines)).isEmpty();
    }

    @Test
    void 상세가_없으면_격리() {
        assertThat(OrderValidator.validate(order(0, 0), List.of())).contains(OrderValidator.NO_DETAIL);
    }

    @Test
    void 상품이_없으면_격리() {
        assertThat(OrderValidator.validate(order(1000, 1000), List.of(line(null, 1000L, 1L, 1000L))))
                .contains(OrderValidator.NULL_PRODUCT);
    }

    @Test
    void 수량이_0이면_격리() {
        assertThat(OrderValidator.validate(order(0, 0), List.of(line(2L, 1000L, 0L, 0L))))
                .contains(OrderValidator.BAD_QUANTITY);
    }

    @Test
    void 줄_금액이_단가x수량과_다르면_격리() {
        assertThat(OrderValidator.validate(order(1100, 1100), List.of(line(2L, 1000L, 1L, 1100L))))
                .contains(OrderValidator.LINE_TOTAL_MISMATCH);
    }

    @Test
    void 주문_총액이_상세_합계와_다르면_격리() {
        assertThat(OrderValidator.validate(order(3000, 3000), List.of(line(2L, 1000L, 2L, 2000L))))
                .contains(OrderValidator.TOTAL_MISMATCH);
    }

    @Test
    void 결제_금액이_주문_총액과_다르면_격리() {
        assertThat(OrderValidator.validate(order(2000, 2500), List.of(line(2L, 1000L, 2L, 2000L))))
                .contains(OrderValidator.PAY_AMOUNT_MISMATCH);
    }
}
