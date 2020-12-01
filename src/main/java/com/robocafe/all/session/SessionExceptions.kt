package com.robocafe.all.session

import com.robocafe.all.application.services.DomainException
import com.robocafe.all.application.services.EntityNotFound

class SessionNotFound: EntityNotFound()
class IncorrectTableStatus: DomainException()