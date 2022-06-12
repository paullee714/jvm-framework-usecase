package com.example.springkotlinsimpleexample.repository

import com.example.springkotlinsimpleexample.domain.OrderModel
import org.springframework.data.repository.CrudRepository

interface OrderRepository : CrudRepository<OrderModel, String> {

    fun findAllByOrderUserName(orderCode: String): List<OrderModel>

    fun findByOrderCode(orderCode: String): OrderModel?
}
