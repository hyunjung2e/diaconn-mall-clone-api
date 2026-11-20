package com.diaconn_mall.website.batch.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 집계 결과를 원본 데이터에서 SQL 로 직접 계산한 값과 행 단위로 대조한다. 하나라도 다르면 Job 을 실패시킨다.
 * 주문별 검증 규칙은 OrderValidator 와 같아야 한다.
 */
@Slf4j
@RequiredArgsConstructor
public class VerifyTasklet implements Tasklet {

    /** 결제 완료 주문마다 정상 여부(1/0)를 계산한다. 상세가 없는 주문은 LEFT JOIN 결과가 NULL 이라 0. */
    private static final String ORDER_CHECK = """
            SELECT p.order_id, DATE(p.pay_date) AS sales_date,
                   COALESCE(o.total_price IS NOT NULL AND p.amount = o.total_price
                        AND SUM(d.product_id IS NULL) = 0
                        AND SUM(d.product_quantity IS NULL OR d.product_quantity <= 0) = 0
                        AND SUM(d.product_price IS NULL OR d.product_total_price IS NULL
                                OR d.product_price * d.product_quantity <> d.product_total_price) = 0
                        AND SUM(d.product_total_price) = o.total_price, 0) AS valid
              FROM c_pay p
              JOIN c_order o ON o.id = p.order_id
              LEFT JOIN c_order_detail d ON d.order_id = p.order_id
             WHERE p.status = 'pay_done' AND p.pay_date >= :start AND p.pay_date < :end
             GROUP BY p.order_id, DATE(p.pay_date), o.total_price, p.amount
            """;

    private static final String EXPECTED_SALES = """
            SELECT c.sales_date, d.product_id, COUNT(DISTINCT d.order_id) AS order_count,
                   SUM(d.product_quantity) AS quantity, SUM(d.product_total_price) AS amount
              FROM (%s) c JOIN c_order_detail d ON d.order_id = c.order_id
             WHERE c.valid = 1
             GROUP BY c.sales_date, d.product_id
            """.formatted(ORDER_CHECK);

    private static final String ACTUAL_SALES = """
            SELECT sales_date, product_id, order_count, quantity, amount
              FROM c_daily_product_sales WHERE sales_date BETWEEN :from AND :to
            """;

    private final NamedParameterJdbcTemplate jdbc;
    private final SettlementPeriod period;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        Map<String, Object> p = Map.of(
                "start", period.start(), "end", period.endExclusive(),
                "from", Date.valueOf(period.fromDate()), "to", Date.valueOf(period.toDate()));

        Map<String, Object> counts = jdbc.queryForMap(
                "SELECT COUNT(*) AS target, COALESCE(SUM(valid), 0) AS valid FROM (" + ORDER_CHECK + ") c", p);
        long target = ((Number) counts.get("target")).longValue();
        long valid = ((Number) counts.get("valid")).longValue();
        Long skippedValue = jdbc.queryForObject(
                "SELECT COUNT(*) FROM c_settlement_skip WHERE sales_date BETWEEN :from AND :to", p, Long.class);
        long skipped = skippedValue == null ? 0 : skippedValue;

        Map<String, List<Long>> expected = rows(EXPECTED_SALES, p);
        Map<String, List<Long>> actual = rows(ACTUAL_SALES, p);
        long matched = expected.entrySet().stream()
                .filter(e -> e.getValue().equals(actual.get(e.getKey()))).count();

        log.info("검증: 대상 주문 {} · 정상 {} · 격리 기대 {} / 실제 {} | 집계 행 기대 {} / 실제 {} / 일치 {}",
                target, valid, target - valid, skipped, expected.size(), actual.size(), matched);
        chunkContext.getStepContext().getStepExecution().getExecutionContext()
                .putString("verify", "target=%d valid=%d skipped=%d rows=%d/%d/%d"
                        .formatted(target, valid, skipped, expected.size(), actual.size(), matched));

        if (expected.size() != actual.size() || matched != expected.size() || skipped != target - valid) {
            throw new IllegalStateException("정산 결과가 원본과 다릅니다 (위 검증 로그 참고)");
        }
        return RepeatStatus.FINISHED;
    }

    /** (판매일|상품) → [주문 수, 수량, 금액] */
    private Map<String, List<Long>> rows(String sql, Map<String, Object> p) {
        Map<String, List<Long>> result = new HashMap<>();
        jdbc.query(sql, p, rs -> {
            result.put(rs.getDate("sales_date") + "|" + rs.getLong("product_id"),
                    List.of(rs.getLong("order_count"), rs.getLong("quantity"), rs.getLong("amount")));
        });
        return result;
    }
}
