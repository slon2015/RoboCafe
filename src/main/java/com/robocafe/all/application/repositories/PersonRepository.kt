package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, String> {
    fun existsByIdAndPartyIdAndPartyEndTimeIsNull(personId: String?, partyId: String?): Boolean
}