
package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.UserDto;
import com.diaconn_mall.website.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/checkemail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
