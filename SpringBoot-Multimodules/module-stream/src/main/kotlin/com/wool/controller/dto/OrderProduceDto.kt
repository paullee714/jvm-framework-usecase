package com.wool.controller.dto

data class OrderProduceDto(
    val orderStoreName: String,
    val orderStoreAddress: String,
    val orderItem: String,
    val orderPrice: String,
    val customerId: Int,
)

/**
 *
{
"orderStoreName": "얌얌김밥",
"orderStoreAddress": "서울특별시 얌얌로 김밥동",
"orderItem": "불고기참치김밥",
"orderPrice": "6500",
"customerId": 2
}
 */
