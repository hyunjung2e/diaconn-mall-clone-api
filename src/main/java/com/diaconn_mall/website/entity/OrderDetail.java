package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "c_order_detail")
@Getter
@Setter
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: order_id → c_order.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    private Long productId;
    private Long productPrice;
    private Long productQuantity;
    private Long productTotalPrice;

    @Column(name = "reg_pgm", nullable = false, length = 100)
    private String regPgm;

    @Column(name = "reg_usr_no", nullable = false)
    private Long regUsrNo;

    @Column(name = "reg_dttm", nullable = false)
    private LocalDateTime regDttm;
}
