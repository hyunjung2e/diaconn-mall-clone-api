package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.*;
import com.diaconn_mall.website.dto.ForgotPasswordRequestDto;
import com.diaconn_mall.website.entity.User;
import com.diaconn_mall.website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public LoginResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        LoginUserDto loginUserDto = new LoginUserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getAddressDetail());
        return new LoginResponseDto("로그인 성공", loginUserDto);
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (user.getPhone() == null || !user.getPhone().equals(phone)) {
            throw new IllegalArgumentException("이메일 또는 휴대폰 정보가 일치하지 않습니다.");
        }

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        // 임시 비밀번호를 DB에 저장
        userRepository.save(user);

        // 이메일로 임시 비밀번호 전송
        emailService.sendTemporaryPassword(email, tempPassword);
    }

    // 임시 비밀번호 생성
    private String generateTemporaryPassword() {
        StringBuilder result = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(TEMP_PASSWORD_CHARS.length());
            result.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return result.toString();
    }
}
