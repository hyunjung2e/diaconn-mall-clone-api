package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Pay pay;
}
