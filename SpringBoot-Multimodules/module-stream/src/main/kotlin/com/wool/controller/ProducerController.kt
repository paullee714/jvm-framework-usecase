package com.wool.controller

import com.wool.controller.dto.OrderProduceDto
import com.wool.producer.OrderProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ProducerController(
    private val orderProducer: OrderProducer
) {

    @PostMapping("/order-produce")
    fun produceOrder(@RequestBody orderDto: OrderProduceDto){
        orderProducer.sendOrderMessage(orderDto)
    }

}
