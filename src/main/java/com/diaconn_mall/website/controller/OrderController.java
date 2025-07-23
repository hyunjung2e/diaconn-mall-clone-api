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
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto, HttpSession session) {
        // 로그인 여부 확인
        LoginUserDto loginUser = (LoginUserDto) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 로그인된 사용자 정보에서 userId 설정
        orderDto.setUserId(loginUser.getId());

        Order savedOrder = orderService.saveOrder(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(@RequestBody OrderDto dto, HttpSession session) {
        LoginUserDto loginUser = (LoginUserDto) session.getAttribute("user");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dto.setUserId(loginUser.getId());

        orderService.updateFullOrder(dto);
        return ResponseEntity.ok("주문 및 관련 정보가 업데이트되었습니다.");
    }
}
