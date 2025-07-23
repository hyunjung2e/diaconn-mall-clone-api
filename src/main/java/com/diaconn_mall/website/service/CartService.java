package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Cart;
import com.diaconn_mall.website.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addToCart(Long userId, Long productId, int count) {
        Optional<Cart> existing = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existing.isPresent()) {
            Cart cart = existing.get();
            cart.setCount(cart.getCount() + count);
            cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setCount(count);
            cartRepository.save(cart);
        }
    }
}
