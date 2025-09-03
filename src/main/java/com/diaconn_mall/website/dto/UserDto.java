package com.diaconn_mall.website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
    private String phone;
    @NotBlank
    @Size(max = 64, message = "비밀번호는 최대 64자까지 가능합니다.")
    private String password;
    private String address;
    private String addressDetail;
}
