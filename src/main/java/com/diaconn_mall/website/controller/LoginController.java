package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.LoginRequest;
import com.diaconn_mall.website.dto.LoginResponse;
import com.diaconn_mall.website.dto.UserDto;
import com.diaconn_mall.website.entity.User;
import com.diaconn_mall.website.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 이메일로 사용자 조회
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getPassword().equals(loginRequest.getPassword())) {
                UserDto userDto = new UserDto();
                userDto.setName(user.getName());
                userDto.setEmail(user.getEmail());
                userDto.setPhone(user.getPhone());
                userDto.setAddress(user.getAddress());

                // 성공 응답
                return ResponseEntity.ok(new LoginResponse("로그인 성공", userDto));
            } else {
                // 비밀번호 오류
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("비밀번호가 일치하지 않습니다.", null));
            }

        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new LoginResponse("해당 이메일의 사용자가 존재하지 않습니다.", null));
        }
    }

// 테스트용 controller
//    @PostMapping("/login")
//    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
//        if ("user".equals(loginRequest.getId()) && "password".equals(loginRequest.getPassword())) {
//            HttpSession session = request.getSession(true);
//            session.setAttribute("user", loginRequest.getId());
//            return "로그인 성공!";
//        } else {
//            return "아이디 또는 비밀번호가 잘못되었습니다.";
//        }
//    }


}