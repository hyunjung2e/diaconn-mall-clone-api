package com.diaconn_mall.website.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private boolean isBanner;
    private String nm;
    private String desc;
    private Integer count;
    private Integer price;
    private String imgUrl;
    private String altText;
    private String state;
    private String category;
}
