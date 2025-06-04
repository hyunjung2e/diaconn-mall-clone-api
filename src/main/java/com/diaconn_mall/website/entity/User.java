package com.diaconn_mall.website.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "c_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(nullable = false)
    private String signout = "N";

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt= LocalDateTime.now();

    @Column(name = "outdate_at")
    private LocalDateTime outdateAt;
}