package com.wool.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class OrderConsumer {

    @KafkaListener(topics = ["order"], groupId = "order-consumer")
    fun consume(message: String){
        println("###########################")
        println(message)
        println("###########################")
    }
}
