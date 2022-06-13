package com.example.springkotlintddsimple.repository

import com.example.springkotlintddsimple.domain.Association
import com.example.springkotlintddsimple.domain.AssociationName
import org.springframework.data.jpa.repository.JpaRepository

interface AssociationRepository : JpaRepository<Association, Long> {

}
