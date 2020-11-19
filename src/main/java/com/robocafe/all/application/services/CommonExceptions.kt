package com.robocafe.all.application.services

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.Exception

@ResponseStatus(HttpStatus.NOT_FOUND)
open class EntityNotFound: Exception()

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class DomainException: Exception()