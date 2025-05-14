package com.diaconn_mall.website.controller;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        if ("user".equals(loginRequest.getId()) && "password".equals(loginRequest.getPassword())) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", loginRequest.getId());
            return "로그인 성공!";
        } else {
            return "아이디 또는 비밀번호가 잘못되었습니다.";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
        return "로그아웃 성공!";
    }

    @GetMapping("/check-session")
    public String checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return "로그인 상태입니다.";
        } else {
            return "로그인하지 않았습니다.";
        }
    }
}

class LoginRequest {
    private String id;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
