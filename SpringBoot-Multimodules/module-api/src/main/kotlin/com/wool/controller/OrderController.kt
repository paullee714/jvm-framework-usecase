package com.wool.controller

import com.wool.service.OrderService
import com.wool.service.dtos.order.OrderRequestDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping("/orders")
    fun getOrders() = orderService.getOrders()

    @PostMapping("/orders")
    fun saveOrders(@RequestBody orderRequestDto: OrderRequestDto) {
        //save order
        return orderService.saveOrder(orderRequestDto)
    }
}
