package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.ProductDto;
import com.diaconn_mall.website.entity.Cart;
import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.repository.CartRepository;
import com.diaconn_mall.website.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
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

    public List<ProductDto> getCartItemsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream()
                .map(cart -> {
                    Optional<Product> optionalProduct = productRepository.findById(cart.getProductId());
                    if (optionalProduct.isEmpty()) return null;

                    Product product = optionalProduct.get();

                    return new ProductDto(
                            product.getId(),
                            product.isBanner(),
                            product.getNm(),
                            product.getContentDesc(),
                            cart.getCount(),
                            product.getPrice(),
                            product.getImgUrl(),
                            product.getAltText(),
                            product.getState(),
                            product.getCategory()
                    );
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteCartItem(Long userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
