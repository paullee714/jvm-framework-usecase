package com.example.springkotlinmongodbsecure.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collation = "friends")
data class Friends(
    @Id
    val id: String,
    val name: String,
    val desc: String,
)
