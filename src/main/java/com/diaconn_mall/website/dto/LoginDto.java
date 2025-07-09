package com.diaconn_mall.website.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
    private Long id;
    private String email;
    private String password;
}