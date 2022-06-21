package com.example.springkotlintddsimple.handler.exception

import com.example.springkotlintddsimple.handler.AssociationErrorResult
import lombok.Getter
import lombok.RequiredArgsConstructor


@Getter
@RequiredArgsConstructor
class AssociationException(duplicatedAssociationFound: AssociationErrorResult) : RuntimeException() {
    val errorResult: AssociationErrorResult = duplicatedAssociationFound
}
