package com.robocafe.all.application.services

class TableNotFree: DomainException()
class TablePersonsCountLowerThanPartyMembersCount: DomainException()

class PartyNotFound: EntityNotFound()
class PartyAlreadyEnded: DomainException()
class PartyAlreadyFull: DomainException()

class PersonNotFound: EntityNotFound()
class PersonNotInParty: DomainException()