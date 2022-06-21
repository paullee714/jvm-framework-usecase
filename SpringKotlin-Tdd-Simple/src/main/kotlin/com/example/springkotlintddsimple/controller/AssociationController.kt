package com.example.springkotlintddsimple.controller

import com.example.springkotlintddsimple.domain.dto.AssociationRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AssociationController {

    @PostMapping("/api/v1/association")
    fun createAssociation(
        @RequestHeader("USER_UUID") user_UUID: String,
        @RequestBody associationRequest: AssociationRequest,
    ): ResponseEntity<AssociationRequest> {

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
