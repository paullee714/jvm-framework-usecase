package com.example.springkotlinsimpleexample.domain

import com.example.springkotlinsimpleexample.domain.dto.CreateOrderModelDTO
import com.example.springkotlinsimpleexample.domain.dto.ReadOrderModelDTO
import org.hibernate.Hibernate
import java.time.OffsetDateTime
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class OrderModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val orderCode: String,
    val orderUserName: String,
    val orderUserPhone: String,
    val orderUserAddress: String,
    val orderUserEmail: String,
    val orderUserComment: String,
    val orderCreatedDate: OffsetDateTime = OffsetDateTime.now(),
) {
    fun getReadOrderDTO(): ReadOrderModelDTO {
        return ReadOrderModelDTO(
            id = id,
            orderCode = orderCode,
            orderUserName = orderUserName,
            orderUserPhone = orderUserPhone,
            orderUserAddress = orderUserAddress,
            orderUserEmail = orderUserEmail,
            orderUserComment = orderUserComment,
            orderCreatedDate = orderCreatedDate
        )
    }

    fun createOrderModelDTO(): CreateOrderModelDTO {
        return CreateOrderModelDTO(
            orderCode = orderCode,
            orderUserName = orderUserName,
            orderUserPhone = orderUserPhone,
            orderUserAddress = orderUserAddress,
            orderUserEmail = orderUserEmail,
            orderUserComment = orderUserComment
        )
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as OrderModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , orderCode = $orderCode , orderUserName = $orderUserName , orderUserPhone = $orderUserPhone , orderUserAddress = $orderUserAddress , orderUserEmail = $orderUserEmail , orderUserComment = $orderUserComment , orderCreatedDate = $orderCreatedDate )"
    }

}
