package com.example.springkotlintddsimple.service

import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import com.example.springkotlintddsimple.handler.AssociationErrorResult
import com.example.springkotlintddsimple.handler.exception.AssociationException
import com.example.springkotlintddsimple.repository.AssociationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AssociationService(private val associationRepository: AssociationRepository) {


    fun registAssociation(userUuid: String, associationName: AssociationName, point: Int): Association? {

        val result: Association? = associationRepository.findByUserUuidAndAssociateName(userUuid, associationName)

        if (result != null) {
            throw AssociationException(AssociationErrorResult.DUPLICATED_ASSOCIATION_FOUND)
        }

        val association: Association = Association(null, associationName, userUuid, point)
        return associationRepository.save(association)
    }

}
