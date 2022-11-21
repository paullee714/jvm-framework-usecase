package com.wool.entity

import com.wool.entity.base.BaseEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val customerId: Long = 0,

    @NotNull
    @Column
    val customerNickName: String,

    @NotNull
    @Column
    val customerAddress: String,
): BaseEntity()
