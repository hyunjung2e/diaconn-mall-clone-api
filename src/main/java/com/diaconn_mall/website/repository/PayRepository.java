package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Order;
import com.diaconn_mall.website.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayRepository extends JpaRepository<Pay, Long> {
    Optional<Pay> findByOrderId(Long orderId);
}
