package com.diaconn_mall.website.batch.settlement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 정산 기간. fromDate·toDate 모두 포함(결제일 기준), 조회는 [from 00:00, toDate+1 00:00). */
public record SettlementPeriod(LocalDate fromDate, LocalDate toDate) {

    public SettlementPeriod {
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("fromDate ≤ toDate 여야 합니다: " + fromDate + " ~ " + toDate);
        }
    }

    public static SettlementPeriod of(String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        return new SettlementPeriod(from, toDate == null || toDate.isBlank() ? from : LocalDate.parse(toDate));
    }

    public LocalDateTime start() {
        return fromDate.atStartOfDay();
    }

    public LocalDateTime endExclusive() {
        return toDate.plusDays(1).atStartOfDay();
    }
}
