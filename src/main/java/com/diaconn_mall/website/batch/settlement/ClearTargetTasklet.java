package com.diaconn_mall.website.batch.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Date;
import java.util.Map;

/**
 * 같은 기간을 다시 정산해도 결과가 두 배로 쌓이지 않도록 기간 내 기존 집계·격리 기록을 지운다.
 * 재시작(같은 JobInstance)일 때는 이미 COMPLETED 인 이 Step 을 건너뛰므로, 이어서 처리 중인 결과를 지우지 않는다.
 */
@Slf4j
@RequiredArgsConstructor
public class ClearTargetTasklet implements Tasklet {

    private final NamedParameterJdbcTemplate jdbc;
    private final SettlementPeriod period;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        Map<String, Object> p = Map.of("from", Date.valueOf(period.fromDate()), "to", Date.valueOf(period.toDate()));
        int sales = jdbc.update("DELETE FROM c_daily_product_sales WHERE sales_date BETWEEN :from AND :to", p);
        int skips = jdbc.update("DELETE FROM c_settlement_skip WHERE sales_date BETWEEN :from AND :to", p);
        log.info("기간 {} ~ {} 기존 집계 {}행 · 격리 기록 {}행 삭제", period.fromDate(), period.toDate(), sales, skips);
        return RepeatStatus.FINISHED;
    }
}
