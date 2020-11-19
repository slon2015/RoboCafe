package com.robocafe.all.application.services

class TableWithSpecifiedNumAlreadyExists: DomainException()

class TableNotFound: EntityNotFound()

class TableAlreadyOccupied: DomainException()
class TableAwaitsCleaningOccupationFailed: DomainException()

class TableOccupiedCleanFailed: DomainException()
class TableAlreadyClean: DomainException()

class TableAlreadyReleased: DomainException()
class TableAwaitsCleaningReleaseFailed: DomainException()