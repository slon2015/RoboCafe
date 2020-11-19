package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.PersonRepository
import com.robocafe.all.domain.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PersonService @Autowired constructor(private val repository: PersonRepository) {
    @Throws(PersonNotFound::class)
    fun findPerson(personId: String): Person {
        return repository.findById(personId).orElseThrow { PersonNotFound() }
    }

    fun personWithActivePartyExists(personId: String, partyId: String) =
            repository.existsByIdAndPartyIdAndPartyEndTimeIsNull(personId, partyId)
}