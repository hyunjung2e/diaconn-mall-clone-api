package com.diaconn_mall.website.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String nm;
    private Integer count;
    private Integer price;
    private String imgUrl;
    private String state;
    private String altText;
    private String category;
    private String contentDesc;
    private String regPgm;
    private Long regUsrNo;
    private LocalDateTime regDttm;
    private String updPgm;
    private Long updUsrNo;
    private LocalDateTime updDttm;
}

