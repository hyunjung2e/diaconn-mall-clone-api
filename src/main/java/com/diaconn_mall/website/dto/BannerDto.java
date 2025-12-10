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
public class BannerDto {
    private Long id;
    private String nm;
    private String imageUrl;
    private String linkUrl;
    private String isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String regPgm;
    private Long regUsrNo;
    private LocalDateTime regDttm;
    private String updPgm;
    private Long updUsrNo;
    private LocalDateTime updDttm;
}

