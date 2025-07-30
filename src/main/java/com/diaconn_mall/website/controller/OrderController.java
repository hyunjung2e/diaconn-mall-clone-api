package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.LoginUserDto;
import com.diaconn_mall.website.dto.OrderDto;
import com.diaconn_mall.website.entity.Order;
import com.diaconn_mall.website.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto, HttpSession session) {
        System.out.println("@@@ 주문 요청 도착");
        // 로그인 여부 확인
        LoginUserDto loginUser = (LoginUserDto) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        orderDto.setUserId(loginUser.getId());
        Order savedOrder = orderService.saveOrder(orderDto);
        return ResponseEntity.ok(savedOrder);
    }
}
