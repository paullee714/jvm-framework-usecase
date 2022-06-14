package com.example.springkotlintddsimple.service


import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import com.example.springkotlintddsimple.handler.AssociationErrorResult
import com.example.springkotlintddsimple.handler.exception.AssociationException
import com.example.springkotlintddsimple.repository.AssociationRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class AssociationServiceTest {

    @MockK
    lateinit var associationRepository: AssociationRepository

    @InjectMockKs
    lateinit var associationService: AssociationService


    var userUuid: String = "uuid-wool-1"
    var associationName: AssociationName = AssociationName.SUBWAY
    var point: Int = 100

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun associateRegistrationAlreadyExists() {
        // given
        every {
            associationRepository.findByUserUuidAndAssociateName(userUuid, associationName)
        } returns Association(
            1,
            associationName,
            userUuid,
            point
        )

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
