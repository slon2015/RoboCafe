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
             val party: Party)

@Entity
class Party(@field:Id val id: String, val tableId: String, val maxMembers: Int) : AbstractAggregateRoot<Party?>() {
    @OneToMany(mappedBy = "party")
    var members: MutableSet<Person> = HashSet()
    var endTime: Instant? = null
    val isMemberless
        get() = members.size == 0

    constructor(id: String, tableId: String, maxMembers: Int, memberCount: Int) : this(id, tableId, maxMembers) {
        var i = 0
        while (i < memberCount && i < maxMembers) {
            val personId = UUID.randomUUID().toString()
            joinPersonToParty(personId)
            i++
        }
    }

    fun isEnded() = endTime == null

    fun joinPersonToParty(id: String) {
        members.add(Person(id, this))
        registerEvent(MemberJoinToParty(this.id, id))
    }

    fun removePersonFromParty(id: String) {
        members.removeIf { p: Person -> p.id == id }
        registerEvent(MemberLeaveParty(this.id, id))
    }

    fun endParty() {
        endTime = Instant.now()
        registerEvent(PartyEnded(id))
    }

    init {
        registerEvent(PartyStarted(id, tableId))
    }


}