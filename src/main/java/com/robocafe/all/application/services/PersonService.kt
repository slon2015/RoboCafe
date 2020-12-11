package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PersonService @Autowired constructor(private val repository: PersonRepository) {
    @Throws(PersonNotFound::class)
    fun getPerson(personId: String): PersonInfo {
        return PersonInfo(repository.findById(personId).orElseThrow { PersonNotFound() })
    }

    fun personWithActivePartyExists(personId: String) =
            repository.existsByIdAndPartyEndTimeIsNull(personId)
}