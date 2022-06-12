package com.example.springkotlinsimpleexample.service

import com.example.springkotlinsimpleexample.domain.dto.CreateOrderModelDTO
import com.example.springkotlinsimpleexample.domain.dto.ReadOrderModelDTO
import com.example.springkotlinsimpleexample.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class OrderService {

    @Autowired
    lateinit var orderRepository: OrderRepository

    fun getAllOrders(): List<ReadOrderModelDTO> {
        val orders = orderRepository.findAll()
        return orders.map { it.getReadOrderDTO() }
    }

    fun getOrderByOrderUserName(orderUserName: String): List<ReadOrderModelDTO> {
        val order = orderRepository.findAllByOrderUserName(orderUserName)
        return order.map { it.getReadOrderDTO() }
    }

    @Transactional
    fun createOrder(order: CreateOrderModelDTO): CreateOrderModelDTO {

        val requestOrderCode: String = order.orderCode
        val oldOrderCode: String = orderRepository.findByOrderCode(requestOrderCode)?.orderCode ?: ""

        orderRepository.findByOrderCode(requestOrderCode).let {
            if (it != null) {
                throw IllegalArgumentException("Order Code is already exist")
            }
        }

        return orderRepository.save(order.toEntity()).createOrderModelDTO()
    }
}
