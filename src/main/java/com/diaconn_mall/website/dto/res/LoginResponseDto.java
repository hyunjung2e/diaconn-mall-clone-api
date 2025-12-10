package com.diaconn_mall.website.dto.res;


import com.diaconn_mall.website.dto.LoginUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String message;
    private LoginUserDto  user;
}
