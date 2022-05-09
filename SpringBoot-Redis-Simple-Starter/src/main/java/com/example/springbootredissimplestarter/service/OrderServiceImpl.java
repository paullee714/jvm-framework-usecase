package com.example.springbootredissimplestarter.service;

import com.example.springbootredissimplestarter.domain.Order;
import com.example.springbootredissimplestarter.exception.OrderNotFoundException;
import com.example.springbootredissimplestarter.exception.OrderStatusException;
import com.example.springbootredissimplestarter.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {

        return orderRepository.save(order);

    }

    @Override
    @Cacheable(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
    }

    @Override
    @CachePut(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public Order updateOrder(Order order, Integer orderId) {
        /*
        order status 변화주기
        status: ready -> processing -> shipped -> delivered
         */

        Order orderObject = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        if (orderObject.getOrderStatus().equals("ready")) {
            orderObject.setOrderStatus("processing");
        } else if (orderObject.getOrderStatus().equals("processing")) {
            orderObject.setOrderStatus("shipped");
        } else if (orderObject.getOrderStatus().equals("shipped")) {
            orderObject.setOrderStatus("delivered");
        } else {
            throw new OrderStatusException("Order Status Cannot Change");
        }

        return orderRepository.save(orderObject);
    }

    @Override
    @CacheEvict(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public void deleteOrder(Integer orderId) {
        Order orderObject = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        orderRepository.delete(orderObject);

    }

    @Override
    @Cacheable(value = "Order", cacheManager = "testCacheManager")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
