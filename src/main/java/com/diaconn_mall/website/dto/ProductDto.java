package com.diaconn_mall.website.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ProductDto {
    private Long id;
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
