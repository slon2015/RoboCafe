package com.robocafe.all.application.services

class PersonsPartyEnded: DomainException()
class ChatNotFound: EntityNotFound()
class ChatMemberNotFound: EntityNotFound()
class MemberAlreadyInChat: DomainException()
class MemberNotInChat: DomainException()