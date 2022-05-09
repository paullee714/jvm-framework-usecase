package com.example.springbootsimpleorderserver.service;

import com.example.springbootsimpleorderserver.domain.OrderDomain;
import com.example.springbootsimpleorderserver.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderDomain createOrder(OrderDomain order) {

        return orderRepository.save(order);
    }

    @Override
    public OrderDomain getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public OrderDomain updateOrder(OrderDomain order, Integer orderId) throws Exception {
        /*
        order status 변화주기
        status: ready -> processing -> shipped -> delivered
         */

        OrderDomain orderObject = orderRepository.findById(orderId).orElse(null);
        if (orderObject == null) {
            throw new Exception("Order not found");
        }

        switch (orderObject.getOrderStatus()) {
            case "ready":
                orderObject.setOrderStatus("processing");
                break;
            case "processing":
                orderObject.setOrderStatus("shipped");
                break;
            case "shipped":
                orderObject.setOrderStatus("delivered");
                break;
            default:
                throw new Exception("order status is not ready");
        }

        return orderRepository.save(orderObject);
    }

    @Override
    public void deleteOrder(Integer orderId) throws Exception {
        OrderDomain orderObject = orderRepository.findById(orderId).orElse(null);
        if (orderObject != null) {
            orderRepository.delete(orderObject);
        } else {
            throw new Exception("Order not found");
        }
    }

    @Override
    public List<OrderDomain> getAllOrders() {
        return orderRepository.findAll();
    }
}
