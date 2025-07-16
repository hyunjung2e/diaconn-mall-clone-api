package com.diaconn_mall.website.dto;

import com.diaconn_mall.website.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String addressDetail;

    // 생성자: User 엔티티를 LoginUserDto로 변환
    public LoginUserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.addressDetail = user.getAddressDetail();
    }
}
