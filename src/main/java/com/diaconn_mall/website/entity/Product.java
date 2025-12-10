package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "c_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long id;
    private String nm;
    private Integer count;
    private Integer price;
    @Column(name = "img_url", columnDefinition = "LONGTEXT")
    private String imgUrl;
    private String state;
    @Column(name = "alt_text")
    private String altText;
    private String category;
    @Column(name = "content_desc")
    private String contentDesc;
    @Column(name = "reg_pgm")
    private String regPgm;
    @Column(name = "reg_usr_no")
    private Long regUsrNo;
    @Column(name = "reg_dttm")
    private java.time.LocalDateTime regDttm;
    @Column(name = "upd_pgm")
    private String updPgm;
    @Column(name = "upd_usr_no")
    private Long updUsrNo;
    @Column(name = "upd_dttm")
    private java.time.LocalDateTime updDttm;
}
