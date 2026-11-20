-- 일별 매출 정산 Batch 용 테이블 (dailySalesSettlementJob)
-- 대상 DB 를 먼저 선택한 뒤 실행한다. (이 파일에는 USE / CREATE DATABASE 가 없다)
-- Spring Batch 메타 테이블(BATCH_*)은 batch 프로필의 initialize-schema: always 로 자동 생성된다.

CREATE TABLE IF NOT EXISTS `c_daily_product_sales` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sales_date` date NOT NULL COMMENT '결제일',
  `product_id` bigint NOT NULL,
  `order_count` bigint NOT NULL DEFAULT 0 COMMENT '이 상품이 포함된 주문 수',
  `quantity` bigint NOT NULL DEFAULT 0,
  `amount` bigint NOT NULL DEFAULT 0,
  `reg_pgm` varchar(100) NOT NULL,
  `reg_usr_no` bigint NOT NULL,
  `reg_dttm` datetime NOT NULL,
  `upd_pgm` varchar(100) DEFAULT NULL,
  `upd_usr_no` bigint DEFAULT NULL,
  `upd_dttm` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_daily_product_sales` (`sales_date`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `c_settlement_skip` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `sales_date` date NOT NULL,
  `reason` varchar(50) NOT NULL COMMENT 'OrderValidator 사유 코드',
  `order_total` bigint DEFAULT NULL,
  `detail_sum` bigint DEFAULT NULL,
  `pay_amount` bigint DEFAULT NULL,
  `reg_pgm` varchar(100) NOT NULL,
  `reg_usr_no` bigint NOT NULL,
  `reg_dttm` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_settlement_skip_date` (`sales_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 정산 Reader 조회용 인덱스: (결제일, 주문 id) 키 기반 페이징을 정렬 없이 읽게 한다 (측정 결과는 README 참고)
CREATE INDEX `idx_pay_status_date_order` ON `c_pay` (`status`, `pay_date`, `order_id`);
