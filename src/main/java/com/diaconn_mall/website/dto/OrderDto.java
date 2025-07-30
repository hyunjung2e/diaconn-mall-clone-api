package com.diaconn_mall.website.dto;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import java.util.List;

@Getter
@Setter

public class OrderDto {
    private Long id;
    private Long totalPrice;
    private LocalDateTime regDate;
    private String address;
    private String addressDetail;
    private String phone;
    private String name;
    private Long userId;
    private String memo;
    private List<OrderDetailDto> orderDetails;
}
