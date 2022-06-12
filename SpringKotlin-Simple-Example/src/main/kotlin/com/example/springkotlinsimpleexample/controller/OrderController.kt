package com.example.springkotlinsimpleexample.controller

import com.example.springkotlinsimpleexample.domain.dto.CreateOrderModelDTO
import com.example.springkotlinsimpleexample.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

    @Autowired
    private lateinit var orderService: OrderService

    @GetMapping(produces = ["application/json"])
    fun getOrders(): ResponseEntity<Any> {
        return ResponseEntity.ok(orderService.getAllOrders())
    }

    @GetMapping(value = ["/{orderUserName}"], produces = ["application/json"])
    fun getOrderByUserName(orderUserName: String): ResponseEntity<Any> {
        return ResponseEntity.ok(orderService.getOrderByOrderUserName(orderUserName))
    }

    @PostMapping()
    fun createOrder(@RequestBody createOrderDTO: CreateOrderModelDTO): ResponseEntity<Any> {
        orderService.createOrder(createOrderDTO)
        return ResponseEntity.ok().body(true)
    }
}
