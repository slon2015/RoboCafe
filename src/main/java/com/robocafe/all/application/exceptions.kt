package com.robocafe.all.application

import com.robocafe.all.application.security.SecurityException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedForThisChat: SecurityException()