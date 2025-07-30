package com.diaconn_mall.website.dto;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
public class PayDto {
    private Long id;
    private Long orderId;
    private DateTime payDate;
    private String status;
    private Long amount;
}
