package com.robocafe.all.application.security

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.UNAUTHORIZED)
open class SecurityException: RuntimeException()
class SecurityObjectNotFound: SecurityException()
class SecurityObjectInvalidated: SecurityException()
