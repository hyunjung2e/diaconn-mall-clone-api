package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.LoginRequest;
import com.diaconn_mall.website.dto.LoginResponse;
import com.diaconn_mall.website.dto.LoginUserDto;
import com.diaconn_mall.website.entity.User;
import com.diaconn_mall.website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        LoginUserDto loginUserDto = new LoginUserDto(user.getId(), user.getName(), user.getEmail());

        return new LoginResponse("로그인 성공", loginUserDto);
    }
}
