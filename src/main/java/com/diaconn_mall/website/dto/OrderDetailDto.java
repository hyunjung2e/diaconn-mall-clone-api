package com.diaconn_mall.website.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class OrderDetailDto {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long productPrice;
    private Long productQuantity;
    private Long productTotalPrice;
}
