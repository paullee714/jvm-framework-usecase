package com.example.springkotlintddsimple.repository

import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
class AssociationRepositoryTest {

    @Autowired
    lateinit var associationRepository: AssociationRepository

    @Test
    fun AssociationRepositoryisNotNull() {
        assertThat(associationRepository).isNotNull()
    }

    @Test
    fun AssociationRepositoryRegistration() {
        // given
        val association: Association = Association(1, AssociationName.KFC, "uuid-wool-1", 0)

        // when
        val result: Association = associationRepository.save(association)

        // then
        println(result)
        assertThat(result.id).isNotNull()
        assertThat(result.id).isEqualTo(1)
        assertThat(result.associateName).isEqualTo(AssociationName.KFC)
        assertThat(result.userUuid).isEqualTo("uuid-wool-1")
        assertThat(result.point).isEqualTo(0)
    }

}
