package com.diaconn_mall.website.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PayDto {
    private Long id;
    private Long orderId;
    private LocalDateTime payDate;
    private String status;
    private Long amount;
    private String regPgm;
    private Long regUsrNo;
    private LocalDateTime regDttm;
}

