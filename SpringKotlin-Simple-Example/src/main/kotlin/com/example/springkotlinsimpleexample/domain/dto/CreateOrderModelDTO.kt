package com.example.springkotlinsimpleexample.domain.dto

import com.example.springkotlinsimpleexample.domain.OrderModel
import java.util.*

data class CreateOrderModelDTO(
    val id: Int? = null,
    val orderCode: String,
    val orderUserName: String,
    val orderUserPhone: String,
    val orderUserAddress: String,
    val orderUserEmail: String,
    val orderUserComment: String,
) {
    fun toEntity(): OrderModel {
        return OrderModel(
            orderCode = orderCode,
            orderUserName = orderUserName,
            orderUserPhone = orderUserPhone,
            orderUserAddress = orderUserAddress,
            orderUserEmail = orderUserEmail,
            orderUserComment = orderUserComment
        )
    }
}
