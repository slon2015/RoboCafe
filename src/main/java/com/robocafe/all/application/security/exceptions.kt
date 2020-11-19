package com.robocafe.all.application.security

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
open class SecurityException: Exception()
class SecurityObjectNotFound: SecurityException()
class SecurityObjectInvalidated: SecurityException()
