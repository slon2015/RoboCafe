package com.robocafe.all.application.services

import org.springframework.beans.factory.annotation.Autowired
import com.robocafe.all.application.repositories.PartyRepository
import kotlin.Throws
import com.robocafe.all.domain.Party
import com.robocafe.all.domain.TableStatus
import org.springframework.stereotype.Service

@Service
class PartyService @Autowired constructor(
        private val repository: PartyRepository,
//        private val tableService: TableService,
        private val personService: PersonService
) {

    private companion object Dsl {
        infix fun Party.operate(operation: Party.() -> Unit) {
            operation(this)
        }
    }

    @Throws(TableNotFound::class, TableNotFree::class, TablePersonsCountLowerThanPartyMembersCount::class)
    fun startParty(tableId: String, partyId: String, maxMembersCount: Int, membersCount: Int): Party {
        val selectedTable = tableService.getTableInfo(tableId)
        if (selectedTable.status != TableStatus.FREE) {
            throw TableNotFree()
        }
        val newParty = Party(partyId, tableId, maxMembersCount, membersCount)
        return repository.save(newParty)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class)
    fun findNotEndedParty(partyId: String): Party {
        val party = repository.findById(partyId).orElseThrow { PartyNotFound() }!!
        if (party.endTime == null) {
            return party
        }
        else {
            throw PartyAlreadyEnded();
        }
    }

    fun notEndedPartyExists(partyId: String) =
            repository.existsByIdAndEndTimeIsNull(partyId)


    @Throws(PartyNotFound::class, PartyAlreadyEnded::class, PartyAlreadyFull::class)
    fun joinPerson(partyId: String, personId: String) {
        val party = findNotEndedParty(partyId)
        if (party.members.size == party.maxMembers) {
            throw PartyAlreadyFull()
        }
        party.joinPersonToParty(personId);
        repository.save(party)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class, PersonNotFound::class, PersonNotInParty::class)
    fun removePersonFromParty(partyId: String, personId: String) {
        val party = findNotEndedParty(partyId)
        val person = personService.findPerson(personId)

        if (party.members.contains(person)) {
            party.removePersonFromParty(personId)
            repository.save(party);
        }
        else {
            throw PersonNotInParty()
        }
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class)
    fun endParty(partyId: String) {
        val party = findNotEndedParty(partyId)
        party.endParty()
        repository.save(party)
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        if (!personService.personWithActivePartyExists(memberId, partyId)) {
            throw PersonNotInParty()
        }
        val party = findNotEndedParty(partyId)
        party.changePersonBalance(memberId, amount)
        repository.save(party)
    }
}