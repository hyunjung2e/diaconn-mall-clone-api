package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.LoginUserDto;
import com.diaconn_mall.website.dto.UserDto;
import com.diaconn_mall.website.entity.User;
import com.diaconn_mall.website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 시 이메일 중복검사
    public boolean isEmailDuplicate(String email) {
        if (email == null || email.trim().isEmpty()) return false; // 이메일이 null이거나 비었으면 중복이 아닌 것으로 처리
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    // 회원가입
    public void registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 내부 보안 가드: BCrypt 한계 보호(사용자 메시지는 일반화)
        if (userDto.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다. 다른 비밀번호를 사용해 주세요.");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAddress(userDto.getAddress());
        userRepository.save(user);
    }

    // 마이페이지 수정
    @Transactional
    public void updateUser(UserDto userDto,  HttpSession session) {
        User existingUser = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        existingUser.setPhone(userDto.getPhone());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            if (userDto.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다. 다른 비밀번호를 사용해 주세요.");
            }
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // 주소가 빈 문자열이면 null 처리
        if (userDto.getAddress() == null || userDto.getAddress().trim().isEmpty()) {
            existingUser.setAddress(null);
            existingUser.setAddressDetail(null);
        } else {
            existingUser.setAddress(userDto.getAddress());
            existingUser.setAddressDetail(userDto.getAddressDetail());
        }
        // DB 저장
        existingUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(existingUser);

        // 세션 유저 정보 업데이트
        LoginUserDto updatedSessionUser = new LoginUserDto(existingUser); // DTO로 변환
        session.setAttribute("user", updatedSessionUser);
    }
}


