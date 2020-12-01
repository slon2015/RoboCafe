package com.robocafe.all.session

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
class PersonRefData(
        @field:Id
        val personId: String,
        @field:ElementCollection
        val orderIds: MutableSet<String> = mutableSetOf(),
        @field:ElementCollection
        val completedOrderIds: MutableSet<String> = mutableSetOf()
)

@Entity
@Table(name = "cafe_sessions")
class Session(
        @field:Id val id: String,
        val tableId: String,
        val partyId: String,
        @field:ElementCollection
        val persons: MutableSet<PersonRefData> = mutableSetOf(),
        @field:ElementCollection
        val chatIds: MutableSet<String> = mutableSetOf(),
        var finished: Boolean = false
)