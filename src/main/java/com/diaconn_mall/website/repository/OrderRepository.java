package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
