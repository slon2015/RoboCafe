package com.robocafe.all.application.services

import org.springframework.beans.factory.annotation.Autowired
import com.robocafe.all.application.repositories.PartyRepository
import kotlin.Throws
import com.robocafe.all.domain.Party
import com.robocafe.all.domain.Person
import org.springframework.stereotype.Service
import java.time.Instant

data class PersonInfo(
        val id: String, val balance: Double
) {
    constructor(data: Person): this(data.id, data.balance)
}

data class PartyInfo(
        val id: String, val tableId: String, val maxMembers: Int,
        val members: Set<PersonInfo>,
        val endTime: Instant?
) {
    constructor(data: Party): this(data.id, data.tableId, data.maxMembers,
            data.members.map { PersonInfo(it) }.toSet(), data.endTime)
}

@Service
class PartyService @Autowired constructor(
        private val repository: PartyRepository,
        private val personService: PersonService
) {

    private companion object Dsl {
        infix fun Party.operate(operation: Party.() -> Unit) {
            operation(this)
        }
    }

    @Throws(TableNotFound::class, TableNotFree::class, TablePersonsCountLowerThanPartyMembersCount::class)
    fun startParty(tableId: String, partyId: String, maxMembersCount: Int, membersCount: Int): PartyInfo {
        val newParty = Party.startParty(partyId, tableId, maxMembersCount, membersCount)
        return PartyInfo(repository.save(newParty))
    }

    fun getActivePartyForTable(tableId: String): PartyInfo {
        return PartyInfo(repository.findByTableIdAndEndTimeIsNull(tableId) ?: throw PartyNotFound())
    }

    fun getPartyForPerson(personId: String): PartyInfo {
        return PartyInfo(repository.findByEndTimeIsNullAndMembersIdEquals(personId)
                ?: throw PartyNotFound())
    }

    private fun findNotEndedParty(partyId: String): Party {
        val party = repository.findById(partyId).orElseThrow { PartyNotFound() }!!
        if (party.endTime == null) {
            return party
        }
        else {
            throw PartyAlreadyEnded();
        }
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class)
    fun getParty(partyId: String): PartyInfo {
        return PartyInfo(findNotEndedParty(partyId))
    }

    fun notEndedPartyExists(partyId: String) =
            repository.existsByIdAndEndTimeIsNull(partyId)


    @Throws(PartyNotFound::class, PartyAlreadyEnded::class, PartyAlreadyFull::class)
    fun joinPerson(partyId: String, personId: String) {
        val party = findNotEndedParty(partyId)
        party.joinPersonToParty(personId);
        repository.save(party)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class, PersonNotFound::class, PersonNotInParty::class)
    fun removePersonFromParty(partyId: String, personId: String) {
        val party = findNotEndedParty(partyId)
        party.removePersonFromParty(personId)
        repository.save(party)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class)
    fun endParty(partyId: String) {
        val party = findNotEndedParty(partyId)
        party.endParty()
        repository.save(party)
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        val party = findNotEndedParty(partyId)
        party.changePersonBalance(memberId, amount)
        repository.save(party)
    }
}