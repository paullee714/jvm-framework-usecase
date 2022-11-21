package com.wool.service

import com.wool.entity.Customer
import com.wool.repository.ModuleCoreCustomerRepository
import com.wool.repository.ModuleCoreOrderRepository
import com.wool.service.dtos.order.OrderRequestDto
import com.wool.service.dtos.order.OrderDto
import org.springframework.stereotype.Service


@Service
class OrderService(
    private val orderRepository: ModuleCoreOrderRepository,
    private val customerRepository: ModuleCoreCustomerRepository
) {

    fun getOrders() = orderRepository.findAll()

    fun saveOrder(orderRequestDto: OrderRequestDto) {
        val customer: Customer = customerRepository.findById(orderRequestDto.customerId).get()
        //customer가 있을 경우
        if (customer != null) {
            val orderDto = OrderDto(
                orderStoreName = orderRequestDto.orderStoreName,
                orderStoreAddress = orderRequestDto.orderStoreAddress,
                orderItem = orderRequestDto.orderItem,
                orderPrice = orderRequestDto.orderPrice,
                customer = customer
            )
            orderRepository.save(orderDto.toEntity())
        } else {
            //customer가 없을 경우
            throw Exception("customer가 없습니다.")
        }

    }

}
