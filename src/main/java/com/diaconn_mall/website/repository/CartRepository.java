package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserIdAndProductId(Long userId, Integer productId);
}
