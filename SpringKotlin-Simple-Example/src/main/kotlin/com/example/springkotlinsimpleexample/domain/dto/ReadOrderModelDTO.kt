package com.example.springkotlinsimpleexample.domain.dto

import java.time.OffsetDateTime
import java.util.*

data class ReadOrderModelDTO(
    val id: Int? = null,
    val orderCode: String,
    val orderUserName: String,
    val orderUserPhone: String,
    val orderUserAddress: String,
    val orderUserEmail: String,
    val orderUserComment: String,
    val orderCreatedDate: OffsetDateTime,
)
