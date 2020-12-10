package com.robocafe.all.application.services

class InvalidParty: DomainException()
class InvalidPerson: DomainException()

class OrderNotFound: EntityNotFound()
class IncorrectPositionStatus: DomainException()
class OrderAlreadyClosed: DomainException()
class OrderAlreadyCompleted: DomainException()
class PositionNotInOrder: DomainException()