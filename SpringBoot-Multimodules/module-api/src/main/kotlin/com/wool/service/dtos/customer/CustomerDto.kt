package com.wool.service.dtos.customer

import com.wool.entity.Customer

data class CustomerDto(
    val customerNickName: String,
    val customerAddress: String,
) {
    fun toEntity() =
        Customer(
            customerNickName = this.customerNickName,
            customerAddress = this.customerAddress
        )
}
