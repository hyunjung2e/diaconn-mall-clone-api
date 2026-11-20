"""정산 Batch 실험용 더미 주문 데이터(CSV) 생성.

사용:
  python3 scripts/batch/generate_settlement_data.py --out /tmp/settlement --orders 1000000 \
      --products "2:12900,3:8900,..."   # 상품 id:가격 (DB 의 c_product 에서 뽑아 넘긴다)
결과: users.csv · orders.csv · details.csv · pays.csv + load.sql (LOAD DATA LOCAL INFILE)

- 결제 완료(pay_done) 95%, 취소(pay_cancel) 5%
- 결제 완료 주문 중 --error-rate 비율(기본 0.1%)에 금액 오류를 일부러 넣는다 → Batch 가 격리해야 할 건수
- 시드 고정이라 같은 인자면 같은 데이터
"""
import argparse
import csv
import os
import random
from datetime import datetime, timedelta

ERROR_TYPES = ["TOTAL_MISMATCH", "NULL_PRODUCT", "BAD_QUANTITY", "LINE_TOTAL_MISMATCH", "PAY_AMOUNT_MISMATCH"]
PGM = "settlement-dummy"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", required=True)
    ap.add_argument("--orders", type=int, default=1_000_000)
    ap.add_argument("--users", type=int, default=10_000)
    ap.add_argument("--start", default="2026-10-01")
    ap.add_argument("--days", type=int, default=30)
    ap.add_argument("--error-rate", type=float, default=0.001)
    ap.add_argument("--products", required=True, help="id:price,id:price,...")
    ap.add_argument("--seed", type=int, default=20261118)
    a = ap.parse_args()

    rnd = random.Random(a.seed)
    products = [tuple(int(x) for x in p.split(":")) for p in a.products.split(",")]
    os.makedirs(a.out, exist_ok=True)
    start = datetime.fromisoformat(a.start)
    span = a.days * 86400
    now = "2026-11-18 00:00:00"

    with open(os.path.join(a.out, "users.csv"), "w", newline="") as f:
        w = csv.writer(f)
        for uid in range(1, a.users + 1):
            w.writerow([uid, f"user{uid}", f"user{uid}@example.com", "N", "dummy", PGM, 0, now, now, now])

    stats = {"pay_done": 0, "pay_cancel": 0, "details": 0}
    errors = {t: 0 for t in ERROR_TYPES}
    with open(os.path.join(a.out, "orders.csv"), "w", newline="") as fo, \
            open(os.path.join(a.out, "details.csv"), "w", newline="") as fd, \
            open(os.path.join(a.out, "pays.csv"), "w", newline="") as fp:
        wo, wd, wp = csv.writer(fo), csv.writer(fd), csv.writer(fp)
        detail_id = 0
        for oid in range(1, a.orders + 1):
            uid = rnd.randint(1, a.users)
            ordered = start + timedelta(seconds=rnd.randrange(span))
            paid = ordered + timedelta(seconds=rnd.randint(1, 120))
            # 기간 밖으로 넘어가는 결제는 마지막 날 안으로
            end = start + timedelta(seconds=span - 1)
            paid = min(paid, end)
            status = "pay_done" if rnd.random() < 0.95 else "pay_cancel"
            stats[status] += 1
            err = rnd.choice(ERROR_TYPES) if status == "pay_done" and rnd.random() < a.error_rate else None
            if err:
                errors[err] += 1

            lines = []
            for i, (pid, price) in enumerate(rnd.sample(products, rnd.randint(1, 4))):
                qty = rnd.randint(1, 3)
                line = [pid, price, qty, price * qty]
                if i == 0 and err == "NULL_PRODUCT":
                    line[0] = None
                if i == 0 and err == "BAD_QUANTITY":
                    line[2], line[3] = 0, 0
                if i == 0 and err == "LINE_TOTAL_MISMATCH":
                    line[3] += 100
                lines.append(line)
            total = sum(l[3] for l in lines)
            if err == "LINE_TOTAL_MISMATCH":
                total = sum(l[1] * l[2] for l in lines)  # 상세 줄만 틀리게
            if err == "TOTAL_MISMATCH":
                total += 1000
            amount = total + 500 if err == "PAY_AMOUNT_MISMATCH" else total

            ts = ordered.strftime("%Y-%m-%d %H:%M:%S")
            wo.writerow([oid, total, ts, f"주소{uid}", "", "010-0000-0000", f"user{uid}", uid, "", PGM, uid, ts])
            for pid, price, qty, lt in lines:
                detail_id += 1
                wd.writerow([detail_id, oid, r"\N" if pid is None else pid, price, qty, lt, PGM, uid, ts])
            wp.writerow([oid, paid.strftime("%Y-%m-%d %H:%M:%S"), status, amount, oid, PGM, uid, ts])
        stats["details"] = detail_id

    load = f"""SET FOREIGN_KEY_CHECKS=0; SET UNIQUE_CHECKS=0;
LOAD DATA LOCAL INFILE '{a.out}/users.csv' INTO TABLE c_user FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  (id, name, email, signout, password, reg_pgm, reg_usr_no, reg_dttm, join_date, updated_at);
LOAD DATA LOCAL INFILE '{a.out}/orders.csv' INTO TABLE c_order FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  (id, total_price, regdate, address, address_detail, phone, name, user_id, memo, reg_pgm, reg_usr_no, reg_dttm);
LOAD DATA LOCAL INFILE '{a.out}/details.csv' INTO TABLE c_order_detail FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  (id, order_id, product_id, product_price, product_quantity, product_total_price, reg_pgm, reg_usr_no, reg_dttm);
LOAD DATA LOCAL INFILE '{a.out}/pays.csv' INTO TABLE c_pay FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  (id, pay_date, status, amount, order_id, reg_pgm, reg_usr_no, reg_dttm);
SET UNIQUE_CHECKS=1; SET FOREIGN_KEY_CHECKS=1;
"""
    with open(os.path.join(a.out, "load.sql"), "w") as f:
        f.write(load)
    print(f"orders={a.orders} details={stats['details']} pay_done={stats['pay_done']} "
          f"pay_cancel={stats['pay_cancel']} injected_errors={sum(errors.values())} {errors}")


if __name__ == "__main__":
    main()
