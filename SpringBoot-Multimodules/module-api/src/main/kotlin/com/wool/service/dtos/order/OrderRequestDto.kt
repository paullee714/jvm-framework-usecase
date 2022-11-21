package com.wool.service.dtos.order

data class OrderRequestDto(
    val orderStoreName: String,
    val orderStoreAddress: String,
    val orderItem: String,
    val orderPrice: Int,
    val customerId: Long,
)
