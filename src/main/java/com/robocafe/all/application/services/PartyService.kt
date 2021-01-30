package com.robocafe.all.application.services

import org.springframework.beans.factory.annotation.Autowired
import com.robocafe.all.application.repositories.PartyRepository
import kotlin.Throws
import com.robocafe.all.domain.Party
import com.robocafe.all.domain.Person
import com.robocafe.all.domain.models.PartyScopedPersonInfo
import com.robocafe.all.domain.models.TableInfo
import org.springframework.stereotype.Service
import java.time.Instant


data class PartyInfo(
        val id: String, val tableId: String, val maxMembers: Int,
        val members: Set<PartyScopedPersonInfo>,
        val endTime: Instant?
) {
    constructor(data: Party): this(data.id, data.tableId, data.maxMembers,
            data.members.map { PartyScopedPersonInfo(it) }.toSet(), data.endTime)
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
    fun startParty(tableInfo: TableInfo, partyId: String, membersCount: Int): PartyInfo {
        val newParty = Party.startParty(
                partyId,
                tableInfo.id,
                tableInfo.tableNumber,
                tableInfo.maxPersons,
                membersCount
        )
        return PartyInfo(repository.save(newParty))
    }

    fun getActivePartyForTable(tableId: String): PartyInfo {
        return PartyInfo(repository.findByTableIdAndEndTimeIsNull(tableId) ?: throw PartyNotFound())
    }

    fun getOptionalPartyForTable(tableId: String): PartyInfo? {
        val party = repository.findByTableIdAndEndTimeIsNull(tableId)
        return if (party == null) null else PartyInfo(party)
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
    fun joinPerson(partyId: String, personId: String, place: Int, tableInfo: TableInfo) {
        val party = findNotEndedParty(partyId)
        party.joinPersonToParty(personId, place, tableInfo.id, tableInfo.tableNumber);
        repository.save(party)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class, PersonNotFound::class, PersonNotInParty::class)
    fun removePersonFromParty(tableNum: Int, partyId: String, personId: String) {
        val party = findNotEndedParty(partyId)
        party.removePersonFromParty(party.tableId, tableNum, personId)
        repository.save(party)
    }

    @Throws(PartyNotFound::class, PartyAlreadyEnded::class)
    fun endParty(tableNum: Int, partyId: String) {
        val party = findNotEndedParty(partyId)
        party.endParty(party.tableId, tableNum)
        repository.save(party)
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        val party = findNotEndedParty(partyId)
        party.changePersonBalance(memberId, amount)
        repository.save(party)
    }
}