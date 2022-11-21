package com.wool.repository

import com.wool.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface ModuleCoreOrderRepository : JpaRepository<Order, Long> {
    fun findOrderByOrderItemContaining(orderItem: String): List<Order>
}
