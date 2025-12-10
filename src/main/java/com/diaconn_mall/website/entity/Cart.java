package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "c_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private  Long productId;

    private int count;

    @Column(name = "reg_pgm", nullable = false, length = 100)
    private String regPgm;

    @Column(name = "reg_usr_no", nullable = false)
    private Long regUsrNo;

    @Column(name = "reg_dttm", nullable = false)
    private LocalDateTime regDttm;

    @Column(name = "upd_pgm", length = 100)
    private String updPgm;

    @Column(name = "upd_usr_no")
    private Long updUsrNo;

    @Column(name = "upd_dttm")
    private LocalDateTime updDttm;

}
