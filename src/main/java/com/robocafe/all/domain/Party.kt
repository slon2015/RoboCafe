package com.robocafe.all.domain

import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
class Person(@field:Id val id: String,
             @field:JoinColumn(name = "party_id")
             @field:ManyToOne
             val party: Party,
             var balance: Double = 0.0)

@Entity
class Party(@field:Id val id: String, val tableId: String, val maxMembers: Int) : AbstractAggregateRoot<Party?>() {
    @OneToMany(mappedBy = "party")
    var members: MutableSet<Person> = HashSet()
    var endTime: Instant? = null
    val isMemberless
        get() = members.size == 0
    val balance
        get() = members.map { it.balance }.sum()

    constructor(id: String, tableId: String, maxMembers: Int, memberCount: Int) : this(id, tableId, maxMembers) {
        var i = 0
        while (i < memberCount && i < maxMembers) {
            val personId = UUID.randomUUID().toString()
            joinPersonToParty(personId)
            i++
        }
    }

    fun isEnded() = endTime == null

    fun joinPersonToParty(memberId: String) {
        members.add(Person(memberId, this))
        registerEvent(MemberJoinToParty(this.id, memberId))
    }

    fun removePersonFromParty(memberId: String) {
        members.removeIf { p: Person -> p.id == memberId }
        registerEvent(MemberLeaveParty(this.id, memberId))
    }

    fun endParty() {
        endTime = Instant.now()
        registerEvent(PartyEnded(id))
    }

    fun changePersonBalance(memberId: String, amount: Double) {
        val member = members.firstOrNull { it.id == memberId }
        if (member != null) {
            member.balance += amount
            registerEvent(MemberBalanceChanged(id, memberId, amount))
        }
    }

    companion object {
        fun startParty(id: String, tableId: String, maxMembers: Int, memberCount: Int): Party {
            val party = Party(id, tableId, maxMembers, memberCount)
            party.registerEvent(PartyStarted(id, tableId))
            return party
        }
    }
}