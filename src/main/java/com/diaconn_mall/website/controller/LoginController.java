package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.LoginRequest;
import com.diaconn_mall.website.dto.LoginResponse;
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            LoginResponse response = authService.login(loginRequest);
            session.setAttribute("user",response.getUser());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(e.getMessage(), null));
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
}
