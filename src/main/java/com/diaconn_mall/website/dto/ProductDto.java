package com.diaconn_mall.website.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
    private Integer id;
    private boolean isBanner;
    private String nm;
    private String contentDesc;
    private Integer count;
    private Integer price;
    private String imgUrl;
    private String altText;
    private String state;
    private String category;
}
