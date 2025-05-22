package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
///Todo. user는 예약어로 테이블명으로 쓰면 오류가 나는 경우가 있다고함 (테이블 자체를 'users로' 변경해야될지 고민)
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String phone;
    private String password;
    private String address;

    @Column(unique = true)
    private String email;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(nullable = false)
    private String signout = "N";

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "outdate_at")
    private LocalDateTime outdateAt;
}