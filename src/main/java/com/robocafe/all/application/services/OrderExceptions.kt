package com.robocafe.all.application.services

class InvalidParty: DomainException()
class InvalidPerson: DomainException()

class OrderNotFound: EntityNotFound()
class IncorrectPositionStatus: DomainException()
class OrderAlreadyClosed: DomainException()
class PositionNotInOrder: DomainException()