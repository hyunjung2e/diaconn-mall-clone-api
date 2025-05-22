package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.UserDto;
import com.diaconn_mall.website.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/checkemail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto) {
        System.out.println(">>> 회원가입 요청 도착: " + userDto);
        userService.registerUser(userDto);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }
}