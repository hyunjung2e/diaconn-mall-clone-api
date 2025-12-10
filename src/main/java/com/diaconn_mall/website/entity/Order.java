package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "c_order")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalPrice;
    private LocalDateTime regdate = LocalDateTime.now();
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private Long userId;
    private String memo;

    @Column(name = "reg_pgm", nullable = false, length = 100)
    private String regPgm;

    @Column(name = "reg_usr_no", nullable = false)
    private Long regUsrNo;

    @Column(name = "reg_dttm", nullable = false)
    private LocalDateTime regDttm;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Pay pay;
}
