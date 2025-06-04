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
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        System.out.println("@@@이메일 중복검사 요청 도착: "+  email);
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(Map.of(
                "isDuplicate", isDuplicate,
                "message", isDuplicate ? "이미 사용중인 이메일입니다." : "사용 가능한 이메일입니다."
        ));
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto) {
        try {
            System.out.println("@@@@회원가입 요청 도착: " + userDto);
            userService.registerUser(userDto);
            return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "회원가입 중 오류가 발생했습니다."));
        }
    }
}