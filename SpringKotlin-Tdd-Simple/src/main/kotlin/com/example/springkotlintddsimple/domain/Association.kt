package com.example.springkotlintddsimple.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
data class Association(
    @Id
    @GeneratedValue
    var id: Long,
    var associateName: AssociationName,
    var userUuid: String,
    var point: Int,
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
