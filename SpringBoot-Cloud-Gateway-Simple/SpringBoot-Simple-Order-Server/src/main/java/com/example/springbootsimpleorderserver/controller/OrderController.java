package com.example.springbootsimpleorderserver.controller;


import com.example.springbootsimpleorderserver.domain.OrderDomain;
import com.example.springbootsimpleorderserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping()
    public OrderDomain createOrder(@RequestBody OrderDomain order) {
        return orderService.createOrder(order);
    }

    @GetMapping()
    public ResponseEntity<List<OrderDomain>> getOrder() {

        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public OrderDomain getOrder(@PathVariable Integer id) {
        return orderService.getOrder(id);
    }

    @PutMapping("/{id}")
    public OrderDomain updateOrder(@PathVariable Integer id, @RequestBody OrderDomain order) throws Exception {
        return orderService.updateOrder(order, id);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Integer id) throws Exception {
        orderService.deleteOrder(id);

        return "Order with id: " + id + " deleted.";
    }
}
