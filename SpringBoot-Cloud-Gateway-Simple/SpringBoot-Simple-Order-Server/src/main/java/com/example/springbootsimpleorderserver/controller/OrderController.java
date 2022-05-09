package com.example.springbootsimpleorderserver.controller;


import com.example.springbootsimpleorderserver.domain.OrderDomain;
import com.example.springbootsimpleorderserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Value("${server.port}")
    private String port;

    @PostMapping()
    public OrderDomain createOrder(@RequestBody OrderDomain order) {
        return orderService.createOrder(order);
    }

    @GetMapping()
    public ResponseEntity<List<OrderDomain>> getOrder() {
        System.out.println("########## SERVER PORT : "+ port);
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public OrderDomain getOrder(@PathVariable Integer id) {
        System.out.println("########## SERVER PORT : "+ port);
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
