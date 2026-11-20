package com.diaconn_mall.website.batch.settlement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * chunk 단위로 주문 상세를 읽어 검증하고, 정상 주문은 상품별 일 매출에 합산, 실패 주문은 격리 테이블에 기록한다.
 * 합산(upsert)과 격리 기록이 chunk 와 같은 트랜잭션이라, 중간에 실패해 재시작해도 커밋된 chunk 는 다시 더해지지 않는다.
 */
@Slf4j
public class SettlementWriter implements ItemWriter<OrderPayRow>, StepExecutionListener {

    static final String PGM = "dailySalesSettlementJob";
    static final long SYSTEM_USER = 0L;

    private static final String DETAIL_COLUMNS =
            "SELECT order_id, product_id, product_price, product_quantity, product_total_price FROM c_order_detail ";

    private static final String UPSERT_SALES = """
            INSERT INTO c_daily_product_sales
                (sales_date, product_id, order_count, quantity, amount, reg_pgm, reg_usr_no, reg_dttm)
            VALUES (:salesDate, :productId, :orderCount, :quantity, :amount, :pgm, :usr, :now)
            ON DUPLICATE KEY UPDATE
                order_count = order_count + VALUES(order_count),
                quantity    = quantity + VALUES(quantity),
                amount      = amount + VALUES(amount),
                upd_pgm = VALUES(reg_pgm), upd_usr_no = VALUES(reg_usr_no), upd_dttm = VALUES(reg_dttm)
            """;

    private static final String INSERT_SKIP = """
            INSERT INTO c_settlement_skip
                (order_id, sales_date, reason, order_total, detail_sum, pay_amount, reg_pgm, reg_usr_no, reg_dttm)
            VALUES (:orderId, :salesDate, :reason, :orderTotal, :detailSum, :payAmount, :pgm, :usr, :now)
            """;

    public enum DetailFetch { BULK, PER_ORDER }

    private final NamedParameterJdbcTemplate jdbc;
    private final DetailFetch detailFetch;
    private final Long failAtOrderId;

    // 구간별 누적 시간(ns): 상세 조회 · 검증·집계 · 집계 upsert · 격리 기록
    private long fetchNanos, aggregateNanos, upsertNanos, skipNanos, upsertRows;

    public SettlementWriter(NamedParameterJdbcTemplate jdbc, DetailFetch detailFetch, Long failAtOrderId) {
        this.jdbc = jdbc;
        this.detailFetch = detailFetch;
        this.failAtOrderId = failAtOrderId;
    }

    @Override
    public void write(Chunk<? extends OrderPayRow> chunk) {
        List<? extends OrderPayRow> orders = chunk.getItems();
        if (failAtOrderId != null && orders.stream().anyMatch(o -> failAtOrderId.equals(o.orderId()))) {
            throw new IllegalStateException("재시작 실험용 강제 실패: order_id=" + failAtOrderId);
        }
        List<Long> ids = orders.stream().map(OrderPayRow::orderId).toList();
        long t0 = System.nanoTime();
        Map<Long, List<OrderLine>> linesByOrder =
                detailFetch == DetailFetch.PER_ORDER ? fetchPerOrder(ids) : fetchBulk(ids);
        long t1 = System.nanoTime();
        fetchNanos += t1 - t0;

        // (판매일, 상품) 순으로 정렬해 upsert 잠금 순서를 고정한다
        Map<SalesKey, long[]> sales = new TreeMap<>();
        List<SqlParameterSource> skips = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (OrderPayRow order : orders) {
            List<OrderLine> lines = linesByOrder.getOrDefault(order.orderId(), List.of());
            LocalDate salesDate = order.payDate().toLocalDate();
            Optional<String> reason = OrderValidator.validate(order, lines);
            if (reason.isPresent()) {
                skips.add(new MapSqlParameterSource()
                        .addValue("orderId", order.orderId())
                        .addValue("salesDate", Date.valueOf(salesDate))
                        .addValue("reason", reason.get())
                        .addValue("orderTotal", order.totalPrice())
                        .addValue("detailSum", OrderValidator.detailSum(lines))
                        .addValue("payAmount", order.payAmount())
                        .addValue("pgm", PGM).addValue("usr", SYSTEM_USER).addValue("now", now));
                continue;
            }
            Set<Long> countedProducts = new HashSet<>();
            for (OrderLine line : lines) {
                long[] agg = sales.computeIfAbsent(new SalesKey(salesDate, line.productId()), k -> new long[3]);
                if (countedProducts.add(line.productId())) {
                    agg[0]++; // 같은 주문에 같은 상품이 여러 줄이어도 주문 수는 1
                }
                agg[1] += line.quantity();
                agg[2] += line.lineTotal();
            }
        }

        long t2 = System.nanoTime();
        aggregateNanos += t2 - t1;
        if (!sales.isEmpty()) {
            SqlParameterSource[] params = sales.entrySet().stream()
                    .map(e -> new MapSqlParameterSource()
                            .addValue("salesDate", Date.valueOf(e.getKey().salesDate()))
                            .addValue("productId", e.getKey().productId())
                            .addValue("orderCount", e.getValue()[0])
                            .addValue("quantity", e.getValue()[1])
                            .addValue("amount", e.getValue()[2])
                            .addValue("pgm", PGM).addValue("usr", SYSTEM_USER).addValue("now", now))
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(UPSERT_SALES, params);
            upsertRows += params.length;
        }
        long t3 = System.nanoTime();
        upsertNanos += t3 - t2;
        if (!skips.isEmpty()) {
            jdbc.batchUpdate(INSERT_SKIP, skips.toArray(SqlParameterSource[]::new));
            log.debug("격리 {}건 (chunk {}건 중)", skips.size(), orders.size());
        }
        skipNanos += System.nanoTime() - t3;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String summary = "detailFetch=%s 상세조회 %.1fs · 검증집계 %.1fs · upsert %.1fs(%d행) · 격리기록 %.1fs".formatted(
                detailFetch, fetchNanos / 1e9, aggregateNanos / 1e9, upsertNanos / 1e9, upsertRows, skipNanos / 1e9);
        log.info("Writer 구간 시간: {}", summary);
        stepExecution.getExecutionContext().putString("writerTiming", summary);
        return stepExecution.getExitStatus();
    }

    /** chunk 의 주문 상세를 IN 한 번으로 읽는다. */
    private Map<Long, List<OrderLine>> fetchBulk(List<Long> ids) {
        Map<Long, List<OrderLine>> result = new HashMap<>();
        if (ids.isEmpty()) {
            return result;
        }
        jdbc.query(DETAIL_COLUMNS + "WHERE order_id IN (:ids) ORDER BY order_id, id",
                Map.of("ids", ids),
                rs -> {
                    OrderLine line = mapLine(rs);
                    result.computeIfAbsent(line.orderId(), k -> new ArrayList<>()).add(line);
                });
        return result;
    }

    /** 비교 실험용: 주문마다 상세를 따로 읽는다 (N+1). */
    private Map<Long, List<OrderLine>> fetchPerOrder(List<Long> ids) {
        Map<Long, List<OrderLine>> result = new HashMap<>();
        for (Long id : ids) {
            result.put(id, jdbc.query(DETAIL_COLUMNS + "WHERE order_id = :id ORDER BY id",
                    Map.of("id", id), (rs, n) -> mapLine(rs)));
        }
        return result;
    }

    private static OrderLine mapLine(ResultSet rs) throws SQLException {
        return new OrderLine(
                rs.getLong("order_id"),
                rs.getObject("product_id", Long.class),
                rs.getObject("product_price", Long.class),
                rs.getObject("product_quantity", Long.class),
                rs.getObject("product_total_price", Long.class));
    }

    record SalesKey(LocalDate salesDate, Long productId) implements Comparable<SalesKey> {
        @Override
        public int compareTo(SalesKey o) {
            int c = salesDate.compareTo(o.salesDate);
            return c != 0 ? c : productId.compareTo(o.productId);
        }
    }
}
