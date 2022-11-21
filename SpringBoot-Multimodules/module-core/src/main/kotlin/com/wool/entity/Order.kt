package com.wool.entity

import com.wool.entity.base.BaseEntity
import org.jetbrains.annotations.NotNull
import java.util.UUID
import javax.persistence.*

@Entity
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val orderId: Long = 0,

    @NotNull
    @Column
    val orderUUID: String = UUID.randomUUID().toString(),

    @NotNull
    @Column
    val orderStoreName: String,

    @NotNull
    @Column
    val orderStoreAddress: String,

    @NotNull
    @Column
    val orderItem: String,

    @NotNull
    @Column
    val orderPrice: Int,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId")
    val customer: Customer,
) : BaseEntity()
