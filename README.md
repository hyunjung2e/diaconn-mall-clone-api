# diaconn-mall-clone

## 커밋 이모티콘 정리

- 수정 및 추가 가능

| 이모티콘                                | 설명                     |
| --------------------------------------- | ------------------------ |
| :tada: `:tada:`                         | new screen               |
| :zap: `:zap:`                           | general fix              |
| :sparkles: `:sparkles:`                 | new feature              |
| :hammer: `:hammer:`                     | refactoring              |
| :bookmark: `:bookmark:`                 | release tag              |
| :bug: `:bug:`                           | bug fix                  |
| :construction: `:construction:`         | work in progress         |
| :ambulance: `:ambulance:`               | critical hot fix         |
| :arrow_up: `:arrow_up:`                 | Upgrading dependencies   |
| :arrow_down: `:arrow_down:`             | Downgrading dependencies |
| :heavy_plus_sign: `:heavy_plus_sign:`   | Adding a dependency      |
| :heavy_minus_sign: `:heavy_minus_sign:` | Removing a dependency    |
| :eyes: `:eyes:`                         | Code Review              |
| :hand: `:hand:`                         | Code Review Request      |

---

## 일별 매출 정산 Batch (`dailySalesSettlementJob`)

결제 완료 주문을 기간 단위로 읽어 **상품별 일 매출**(`c_daily_product_sales`)로 집계하는 Spring Batch Job.
주문 총액은 클라이언트가 보낸 값을 그대로 저장하므로, 정산 때 **결제 금액 · 주문 총액 · 상세 합계**를 대조해 맞지 않는 주문은 집계에서 빼고 `c_settlement_skip` 에 사유와 함께 남긴다.

```
settlementClearStep   기간 내 기존 집계·격리 기록 삭제 (같은 기간 재실행 시 두 배로 쌓이지 않게)
settlementStep        결제 완료 주문 읽기 → chunk 단위 상세 조회·검증 → 상품별 합산 upsert / 실패 주문 격리
settlementVerifyStep  집계 결과를 원본에서 SQL 로 계산한 값과 행 단위 대조, 다르면 Job 실패
```

- 코드: `src/main/java/.../batch/settlement/` · 테이블·인덱스: `sql_dump/batch_settlement.sql` · 더미 데이터: `scripts/batch/generate_settlement_data.py`
- `batch` 프로필에서만 Job 이 등록된다 (웹 서버 실행에는 영향 없음)

### 실행

```bash
# 1) 실험용 DB 에 스키마 + 정산 테이블 (덤프 파일의 CREATE DATABASE / USE 줄은 빼고 넣는다)
grep -v "^CREATE DATABASE\|^USE " sql_dump/251210dump.sql | mysql -u <user> -p healthcare_mall_batch
mysql -u <user> -p healthcare_mall_batch < sql_dump/batch_settlement.sql

# 2) 더미 주문 100만 건 (상품 id:가격 목록을 DB 에서 뽑아 넘긴다)
python3 scripts/batch/generate_settlement_data.py --out /tmp/settlement --products "$(mysql -N -u <user> -p healthcare_mall_batch \
  -e "select group_concat(concat(id,':',price) order by id) from c_product")"
mysql --local-infile=1 -u <user> -p healthcare_mall_batch < /tmp/settlement/load.sql

# 3) 정산 (DB 계정은 각자의 application.yml, DB 이름은 application-batch.yml)
./gradlew bootJar
java -jar build/libs/website-0.0.1-SNAPSHOT.jar --spring.profiles.active=batch \
  fromDate=2026-10-01 toDate=2026-10-30 runId=1
```

| 파라미터 | 값 |
|---|---|
| `fromDate` · `toDate` | 정산 기간 (결제일 기준, 둘 다 포함) |
| `runId` | 같은 기간을 다시 돌릴 때 바꾼다 (같은 값으로 다시 실행하면 실패 지점부터 **재시작**) |
| `chunkSize` | 기본 1000 |
| `sortKey` · `reader` · `detailFetch` | 비교 실험용 (`orderId` · `jpa` · `perOrder`) |
| `failAtOrderId` | 재시작 실험용 강제 실패 (`failAtOrderId=123,java.lang.String,false` 처럼 non-identifying 으로) |

### 측정 (로컬 MySQL 8.4, 주문 100만 건 · 상세 250만 건 · 30일, 결제 완료 95만 건, 일부러 넣은 금액 오류 932건)

모든 실행에서 검증 Step 통과: 격리 932건 = 넣은 오류 수(사유 5종 모두 일치), 집계 1,440행 = 원본 SQL 결과.

| 비교 | 결과 |
|---|---|
| **읽는 순서 + 인덱스** — `order_id` 순서·인덱스 없음 → `(결제일, 주문 id)` 키 기반 + `c_pay(status, pay_date, order_id)` | 정산 Step **31분 36초 → 57.9초**, 검증 Step 15분 → 37.7초 |
| 　원인 | `order_id` 순서로 읽으면 chunk(1,000건)가 30일에 흩어져 chunk 마다 약 1,185개 (일자, 상품) 행을 upsert (총 113만 행, 1.18초/chunk). 결제일 순서로 읽으면 chunk 가 하루 안에 모여 총 **4.7만 행** |
| chunk 크기 100 / 1,000 / 5,000 | 160.7초 / 57.9초 / 51.6초 → 1,000 사용 (5,000 대비 차이 작고, 실패 시 되돌리는 단위가 작음) |
| 상세 조회 — 주문마다(N+1) vs chunk 단위 `IN` 한 번 | 상세 조회 80.8초 vs 14.2초 (Step 123.5초 vs 57.9초) |
| 페이징 — OFFSET(JPA) vs 키 기반, 90만 번째 행 이후 한 페이지 | 4.88초 · 195만 행 읽음 vs **0.13초 · 2,001행** |
| 재시작 — 47.5만 건 지점 강제 실패 후 같은 파라미터로 재실행 | 커밋된 475 chunk 유지, 남은 **475,169건만** 처리, 검증 통과 |
| 같은 기간 재실행 (멱등성) | 기존 1,440행·격리 932행 삭제 후 다시 집계, 합계 동일 |
| 참고: SQL `GROUP BY` 한 번 | 20.1초. 단 검증·격리·재시작 없음 (금액 오류 주문까지 집계되어 1,470행) |

> 측정은 노트북에서 1회씩. 절대값보다 같은 데이터에서의 **상대 비교**로 본다.
