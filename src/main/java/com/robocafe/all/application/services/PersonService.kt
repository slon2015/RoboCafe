package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.PersonRepository
import com.robocafe.all.domain.models.PartyScopedPersonInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PersonService @Autowired constructor(private val repository: PersonRepository) {
    @Throws(PersonNotFound::class)
    fun getPerson(personId: String): PartyScopedPersonInfo {
        return PartyScopedPersonInfo(repository.findById(personId).orElseThrow { PersonNotFound() })
    }

    fun getPersonFromPartyByPlace(partyId: String, place: Int) =
            PartyScopedPersonInfo(repository.findByPartyIdAndPlaceOnTable(partyId, place) ?: throw PersonNotFound())

    fun personWithActivePartyExists(personId: String) =
            repository.existsByIdAndPartyEndTimeIsNull(personId)
}