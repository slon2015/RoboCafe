package com.robocafe.all.session

import com.robocafe.all.application.services.DomainException
import com.robocafe.all.application.services.EntityNotFound

class SessionNotFound: EntityNotFound()
class IncorrectTableStatus: DomainException()

class PartyAlreadyFull: DomainException()
class PersonNotInParty: DomainException()

class MemberWithoutDomainId: DomainException()
class MembersPartyEnded: DomainException()
class InvalidPersonId: DomainException()
class MemberAlreadyInChat: DomainException()
class MemberNotInChat: DomainException()