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
    private Integer id;
    @Column(name = "is_banner")
    private boolean isBanner;
    private String nm;
    private String desc;
    private Integer count;
    private Integer price;
    @Column(name = "img_url", columnDefinition = "LONGTEXT")
    private String imgUrl;
    @Column(name = "alt_text")
    private String altText;
    private String state;
    private String category;
}
