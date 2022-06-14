package com.example.springkotlintddsimple.service

import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import com.example.springkotlintddsimple.handler.AssociationErrorResult
import com.example.springkotlintddsimple.handler.exception.AssociationException
import com.example.springkotlintddsimple.repository.AssociationRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn


@ExtendWith(MockitoExtension::class)
class AssociationServiceTest {

    @Mock
    lateinit var associationRepository: AssociationRepository

    @InjectMocks
    lateinit var associationService: AssociationService

    var userUuid: String = "uuid-wool-1"
    var associationName: AssociationName = AssociationName.SUBWAY
    var point: Int = 100


    @Test
    fun associateRegistrationAlreadyExists() {
        // given
        doReturn(Association(1, associationName, userUuid, point)).`when`(associationRepository)
            .findByUserUuidAndAssociateName(userUuid, associationName)

        // when
        val result: AssociationException = assertThrows(
            AssociationException::class.java
        ) {
            associationService.registAssociation(userUuid, associationName, point)
        }

        // then
        assertThat(result.errorResult).isEqualTo(AssociationErrorResult.DUPLICATED_ASSOCIATION_FOUND)
    }

}
