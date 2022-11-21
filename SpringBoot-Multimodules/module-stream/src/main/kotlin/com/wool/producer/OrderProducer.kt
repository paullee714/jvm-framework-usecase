package com.wool.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.wool.controller.dto.OrderProduceDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    final val KAFKA_ORDER_TOPIC: String = "order"

    fun sendOrderMessage(message: OrderProduceDto){
        // OrderProduceDto를 json serialize
        val obm:ObjectMapper = ObjectMapper()
        val jsomMessage = obm.writeValueAsString(message)

        kafkaTemplate.send(KAFKA_ORDER_TOPIC, jsomMessage)
    }

}
