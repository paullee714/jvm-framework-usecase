package com.example.springbootsimplejpa.controller;


import com.example.springbootsimplejpa.domain.OrderModel;
import com.example.springbootsimplejpa.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderService orderService;


    @GetMapping
    public List<OrderModel> findAllOrderController() {
        return orderService.getAllOrder();
    }

    @PostMapping
    public OrderModel saveOrderController(@RequestBody OrderModel order) {
        return orderService.saveOrder(order);
    }


}
