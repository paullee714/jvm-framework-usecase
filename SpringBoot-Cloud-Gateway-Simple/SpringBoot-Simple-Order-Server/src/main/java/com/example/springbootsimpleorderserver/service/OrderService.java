package com.example.springbootsimpleorderserver.service;

import com.example.springbootsimpleorderserver.domain.OrderDomain;

import java.util.List;

public interface OrderService {

    public OrderDomain createOrder(OrderDomain order);

    public OrderDomain getOrder(Integer orderId);

    public OrderDomain updateOrder(OrderDomain order, Integer orderId) throws Exception;

    public void deleteOrder(Integer orderId) throws Exception;

    public List<OrderDomain> getAllOrders();

}
