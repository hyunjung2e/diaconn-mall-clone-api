package com.diaconn_mall.website.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class OrderDto {
    private Long id;
    private Long totalPrice;
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private Long userId;
    private String memo;

    private Long productPrice;
    private Long productQuantity;
}
