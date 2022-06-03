package com.example.springbootsimplejpa.service;

import com.example.springbootsimplejpa.domain.OrderModel;
import com.example.springbootsimplejpa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public OrderModel saveOrder(OrderModel orderDto) {
        return orderRepository.save(orderDto);
    }

    public List<OrderModel> saveOrderList(List<OrderModel> orderDtoList) {
        return orderRepository.saveAll(orderDtoList);
    }

    public List<OrderModel> getAllOrder() {
        return orderRepository.findAll();
    }

    public Optional<OrderModel> getOrderById(int id) {
        return orderRepository.findById(String.valueOf(id));
    }

    public Optional<OrderModel> getOrderByCode(String orderCode) {
        return Optional.ofNullable(orderRepository.findByOrderCode(orderCode));
    }
}
