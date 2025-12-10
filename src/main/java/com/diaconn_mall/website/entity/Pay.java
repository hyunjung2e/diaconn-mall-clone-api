package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "c_pay")
@Getter
@Setter
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime payDate;
    private String status;
    private Long amount;

    // FK: order_id → c_order.id
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "reg_pgm")
    private String regPgm;

    @Column(name = "reg_usr_no")
    private Long regUsrNo;

    @Column(name = "reg_dttm")
    private LocalDateTime regDttm;
}
