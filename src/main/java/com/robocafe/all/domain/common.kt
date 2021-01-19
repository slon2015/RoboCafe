package com.robocafe.all.domain

interface DomainEvent {
    fun convertForHall(): Any = this
    fun convertForChat(): Any = this
    fun convertForParty(): Any = this
    fun convertForPerson(): Any = this
    fun convertForTable(): Any = this
}
interface ChatDomainEvent: DomainEvent {
    val chatId: String
}
interface PartyDomainEvent: DomainEvent {
    val partyId: String
}
interface TableDomainEvent: DomainEvent {
    val tableId: String
}
interface PersonDomainEvent: DomainEvent {
    val personId: String
}