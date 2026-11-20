package com.diaconn_mall.website.batch.settlement;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 일별 매출 정산 Job. batch 프로필에서만 등록되어 웹 서버 실행에는 영향이 없다.
 *
 * <pre>
 * Job 파라미터 (모두 String)
 *   fromDate, toDate   정산 기간 (yyyy-MM-dd, 결제일 기준, 둘 다 포함)
 *   runId              같은 기간을 다시 돌릴 때 바꾸는 값
 *   chunkSize          기본 1000
 *   reader             jdbc(키 기반 페이징, 기본) | jpa(OFFSET 페이징, 비교용)
 *   sortKey            payDate(결제일+주문 id, 기본) | orderId(비교용)
 *   detailFetch        bulk(chunk 단위 IN, 기본) | perOrder(주문마다 조회, 비교용)
 *   failAtOrderId      재시작 실험용 강제 실패 지점 (non-identifying 으로 넘긴다)
 * </pre>
 */
@Configuration
@Profile("batch")
public class SettlementJobConfig {

    public static final String JOB_NAME = "dailySalesSettlementJob";

    @Bean
    public Job dailySalesSettlementJob(JobRepository jobRepository,
                                       Step settlementClearStep, Step settlementStep, Step settlementVerifyStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(settlementClearStep)
                .next(settlementStep)
                .next(settlementVerifyStep)
                .build();
    }

    @Bean
    public Step settlementClearStep(JobRepository jobRepository, PlatformTransactionManager tx,
                                    ClearTargetTasklet clearTargetTasklet) {
        return new StepBuilder("settlementClearStep", jobRepository).tasklet(clearTargetTasklet, tx).build();
    }

    @Bean
    @JobScope
    public Step settlementStep(JobRepository jobRepository, PlatformTransactionManager tx,
                               ItemStreamReader<OrderPayRow> settlementReader,
                               SettlementWriter settlementWriter,
                               @Value("#{jobParameters['chunkSize'] ?: '1000'}") String chunkSize) {
        return new StepBuilder("settlementStep", jobRepository)
                .<OrderPayRow, OrderPayRow>chunk(Integer.parseInt(chunkSize), tx)
                .reader(settlementReader)
                .writer(settlementWriter)
                .listener(settlementWriter)
                .build();
    }

    @Bean
    public Step settlementVerifyStep(JobRepository jobRepository, PlatformTransactionManager tx,
                                     VerifyTasklet verifyTasklet) {
        return new StepBuilder("settlementVerifyStep", jobRepository).tasklet(verifyTasklet, tx).build();
    }

    @Bean
    @StepScope
    public ClearTargetTasklet clearTargetTasklet(NamedParameterJdbcTemplate jdbc,
                                                 @Value("#{jobParameters['fromDate']}") String fromDate,
                                                 @Value("#{jobParameters['toDate']}") String toDate) {
        return new ClearTargetTasklet(jdbc, SettlementPeriod.of(fromDate, toDate));
    }

    @Bean
    @StepScope
    public VerifyTasklet verifyTasklet(NamedParameterJdbcTemplate jdbc,
                                       @Value("#{jobParameters['fromDate']}") String fromDate,
                                       @Value("#{jobParameters['toDate']}") String toDate) {
        return new VerifyTasklet(jdbc, SettlementPeriod.of(fromDate, toDate));
    }

    @Bean
    @StepScope
    public ItemStreamReader<OrderPayRow> settlementReader(DataSource dataSource, EntityManagerFactory emf,
                                                          @Value("#{jobParameters['fromDate']}") String fromDate,
                                                          @Value("#{jobParameters['toDate']}") String toDate,
                                                          @Value("#{jobParameters['chunkSize'] ?: '1000'}") String chunkSize,
                                                          @Value("#{jobParameters['reader'] ?: 'jdbc'}") String reader,
                                                          @Value("#{jobParameters['sortKey'] ?: 'payDate'}") String sortKey) {
        int pageSize = Integer.parseInt(chunkSize);
        SettlementPeriod settlementPeriod = SettlementPeriod.of(fromDate, toDate);
        Map<String, Object> params = Map.of("start", settlementPeriod.start(), "end", settlementPeriod.endExclusive());

        if ("jpa".equals(reader)) {
            // 비교용: setFirstResult(OFFSET) 방식이라 뒤 페이지로 갈수록 앞의 행을 다시 훑는다
            return new JpaPagingItemReaderBuilder<OrderPayRow>()
                    .name("settlementReader")
                    .entityManagerFactory(emf)
                    .queryString("""
                            SELECT new com.diaconn_mall.website.batch.settlement.OrderPayRow(o.id, o.totalPrice, p.amount, p.payDate)
                              FROM Pay p JOIN p.order o
                             WHERE p.status = 'pay_done' AND p.payDate >= :start AND p.payDate < :end
                             ORDER BY p.payDate, o.id""")
                    .parameterValues(params)
                    .pageSize(pageSize)
                    .build();
        }

        MySqlPagingQueryProvider query = new MySqlPagingQueryProvider();
        query.setFromClause("c_pay p JOIN c_order o ON o.id = p.order_id");
        query.setWhereClause("p.status = 'pay_done' AND p.pay_date >= :start AND p.pay_date < :end");
        if ("orderId".equals(sortKey)) {
            // 비교용: order_id 순서. 결제일 조건과 정렬 기준이 달라 페이지마다 대상 전체를 다시 정렬한다
            query.setSelectClause("p.order_id, o.total_price, p.amount AS pay_amount, p.pay_date");
            query.setSortKeys(Map.of("p.order_id", Order.ASCENDING));
        } else {
            // 기본: (결제일, 주문 id) 키 기반 페이징. 인덱스 c_pay(status, pay_date, order_id) 순서 그대로 읽고 LIMIT 에서 멈춘다
            query.setSelectClause("p.pay_date, p.order_id, o.total_price, p.amount AS pay_amount");
            Map<String, Order> keys = new LinkedHashMap<>();
            keys.put("p.pay_date", Order.ASCENDING);
            keys.put("p.order_id", Order.ASCENDING);
            query.setSortKeys(keys);
        }
        return new JdbcPagingItemReaderBuilder<OrderPayRow>()
                .name("settlementReader")
                .dataSource(dataSource)
                .queryProvider(query)
                .parameterValues(params)
                .pageSize(pageSize)
                .rowMapper(new DataClassRowMapper<>(OrderPayRow.class))
                .build();
    }

    @Bean
    @StepScope
    public SettlementWriter settlementWriter(NamedParameterJdbcTemplate jdbc,
                                             @Value("#{jobParameters['detailFetch'] ?: 'bulk'}") String detailFetch,
                                             @Value("#{jobParameters['failAtOrderId']}") String failAtOrderId) {
        SettlementWriter.DetailFetch mode = "perOrder".equals(detailFetch)
                ? SettlementWriter.DetailFetch.PER_ORDER : SettlementWriter.DetailFetch.BULK;
        Long failAt = failAtOrderId == null || failAtOrderId.isBlank() ? null : Long.valueOf(failAtOrderId);
        return new SettlementWriter(jdbc, mode, failAt);
    }
}
