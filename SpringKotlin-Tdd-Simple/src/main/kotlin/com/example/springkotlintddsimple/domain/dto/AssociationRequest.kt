package com.example.springkotlintddsimple.domain.dto

import com.example.springkotlintddsimple.domain.AssociationName

data class AssociationRequest(
    val point: Int,
    val associationName: AssociationName,
)
