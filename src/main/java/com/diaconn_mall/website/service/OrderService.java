package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.OrderDto;
import com.diaconn_mall.website.dto.OrderDetailDto;
import com.diaconn_mall.website.entity.Order;
import com.diaconn_mall.website.entity.OrderDetail;
import com.diaconn_mall.website.entity.Pay;
import com.diaconn_mall.website.repository.OrderRepository;
import com.diaconn_mall.website.repository.OrderDetailRepository;
import com.diaconn_mall.website.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PayRepository payRepository;
    
    // 주문 저장
    @Transactional
    public Order saveOrder(OrderDto orderDto) {
        // 1. c_order 업데이트
        Order order = new Order();
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setRegdate(LocalDateTime.now());
        order.setAddress(orderDto.getAddress());
        order.setAddressDetail(orderDto.getAddressDetail());
        order.setPhone(orderDto.getPhone());
        order.setName(orderDto.getName());
        order.setUserId(orderDto.getUserId());
        order.setMemo(orderDto.getMemo());

        // order ID 확보
        Order savedOrder = orderRepository.save(order);

        // 2. c_order_datail 업데이트
        // NullPointerException 대비
        List<OrderDetail> orderDetails = Optional.ofNullable(orderDto.getOrderDetails())
                .orElse(Collections.emptyList())
                .stream()
                .map(detailDto -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(savedOrder);
                    detail.setProductId(detailDto.getProductId());
                    detail.setProductPrice(detailDto.getProductPrice());
                    detail.setProductQuantity(detailDto.getProductQuantity());
                    detail.setProductTotalPrice(detailDto.getProductPrice() * detailDto.getProductQuantity());
                    return detail;
                })
                .toList();
        orderDetailRepository.saveAll(orderDetails);

        // 3. c_pay 업데이트
        Pay pay = payRepository.findByOrderId(savedOrder.getId())
                .orElse(new Pay());
        pay.setPayDate(LocalDateTime.now());
        pay.setStatus("pay_done");
        pay.setAmount(savedOrder.getTotalPrice());
        pay.setOrder(savedOrder);

        payRepository.save(pay);
        return savedOrder;
    }
}