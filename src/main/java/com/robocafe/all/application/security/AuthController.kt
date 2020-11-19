package com.robocafe.all.application.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LoginModel(val login: String, val password: String)


@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(
        private val securityService: SecurityService,
        private val jwtProvider: JwtProvider,
        private val authenticationRepository: AuthenticationRepository
) {

    @PostMapping
    fun authByPassword(@RequestBody body: LoginModel): ResponseEntity<Void> {
        val auth = authenticationRepository.findByLogin(body.login) ?: return ResponseEntity.notFound().build()
        if (auth.password != body.password) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val headers = HttpHeaders()
        headers.setBearerAuth(
                jwtProvider.generateToken(SecurityObjectInfo(auth.soId))
        )
        return ResponseEntity.ok().headers(headers).build()
    }
}