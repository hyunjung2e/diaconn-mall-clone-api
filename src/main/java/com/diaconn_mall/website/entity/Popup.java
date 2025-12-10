package com.diaconn_mall.website.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "c_popup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Popup {
    @Id
    private Long id;

    private String nm;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reg_pgm")
    private String regPgm;

    @Column(name = "reg_usr_no")
    private Long regUsrNo;

    @Column(name = "reg_dttm")
    private LocalDateTime regDttm;

    @Column(name = "upd_pgm")
    private String updPgm;

    @Column(name = "upd_usr_no")
    private Long updUsrNo;

    @Column(name = "upd_dttm")
    private LocalDateTime updDttm;
}



