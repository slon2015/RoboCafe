package com.robocafe.core.repositories

import com.robocafe.core.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, String> {
    fun existsByIdAndPartyEndTimeIsNull(personId: String): Boolean
    fun findByPartyIdAndPlaceOnTable(partyId: String, place: Int): Person?
}