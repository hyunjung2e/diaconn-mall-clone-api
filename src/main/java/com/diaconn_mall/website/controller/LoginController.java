package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.LoginDto;
import com.diaconn_mall.website.dto.LoginResponseDto;
import com.diaconn_mall.website.dto.LoginUserDto;
import com.diaconn_mall.website.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpSession session) {
        try {
            LoginResponseDto response = authService.login(loginDto);
            session.setAttribute("user",response.getUser());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto(e.getMessage(), null));
        }
    }

    @GetMapping("/userCheck")
    public ResponseEntity<?> userCheck(HttpSession session) {
        LoginUserDto user = (LoginUserDto) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인된 사용자가 없습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
}
