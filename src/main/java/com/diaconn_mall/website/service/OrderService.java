package com.diaconn_mall.website.service;

import com.diaconn_mall.website.dto.OrderDto;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PayRepository payRepository;

    /**
     * 주문 저장
     */
    @Transactional
    public Order saveOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setAddress(orderDto.getAddress());
        order.setAddressDetail(orderDto.getAddressDetail());
        order.setPhone(orderDto.getPhone());
        order.setName(orderDto.getName());
        order.setUserId(orderDto.getUserId());
        order.setMemo(orderDto.getMemo());

        return orderRepository.save(order);
    }

    @Transactional
    public void updateFullOrder(OrderDto dto) {
        // 주문 정보 조회 및 업데이트
        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.setAddress(dto.getAddress());
        order.setAddressDetail(dto.getAddressDetail());
        order.setMemo(dto.getMemo());
        order.setPhone(dto.getPhone());
        order.setTotalPrice(dto.getTotalPrice());
        orderRepository.save(order);

        // 주문 상세 정보 수정
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        for (OrderDetail detail : details) {
            detail.setProductPrice(dto.getProductPrice());
            detail.setProductQuantity(dto.getProductQuantity());
            detail.setProductTotalPrice(dto.getProductPrice() * dto.getProductQuantity());
        }
        orderDetailRepository.saveAll(details);

        // 결제 정보 수정
        Pay pay = payRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
        pay.setAmount(dto.getTotalPrice());
        pay.setStatus("UPDATED");
        pay.setPayDate(LocalDateTime.now());
        payRepository.save(pay);
    }

}