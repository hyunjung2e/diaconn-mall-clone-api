package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.AddToCartRequest;
import com.diaconn_mall.website.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(request.getUserId(), request.getProductId(), request.getCount());
        return ResponseEntity.ok(Map.of("message", "장바구니에 담았습니다"));
    }
}
