package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.Party

data class PartyStartModel(val tableId: String, val membersCount: Int?)
class PartyInfo(party: Party) {
    val partyId: String = party.id
    val members: Array<String> = party.members.map { person -> person.id }.toTypedArray()
}