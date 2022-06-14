package com.example.springkotlintddsimple.handler

import lombok.Getter
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus


@Getter
@RequiredArgsConstructor
enum class AssociationErrorResult(val status: HttpStatus, val message: String) {
    DUPLICATED_ASSOCIATION_FOUND(HttpStatus.BAD_REQUEST, "이미 존재합니다"),
}
