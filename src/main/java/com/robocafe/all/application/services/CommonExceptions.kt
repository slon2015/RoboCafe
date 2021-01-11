package com.robocafe.all.application.services

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.Exception
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
open class EntityNotFound: RuntimeException()

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class DomainException: RuntimeException()