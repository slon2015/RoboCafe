package com.robocafe.all.application.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

data class SecurityObjectInfo(val securityObjectId: String) {
    constructor(securityObject: Object): this(securityObject.id)
}

@Component
class JwtProvider @Autowired constructor(
        @Value("\${jwt.secret}")
        private val secret: String
) {
    private val key
        get() = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun generateToken(info: SecurityObjectInfo): String {
        val expirationDate = Date.from(LocalDate.now().plusYears(100L)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())
        return Jwts.builder()
                .setExpiration(expirationDate)
                .claim("securityObjectId", info.securityObjectId)
                .signWith(key)
                .compact()
    }

    fun getInfoFromToken(token: String): SecurityObjectInfo {
        val claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).body

        return SecurityObjectInfo(
                claims.get("securityObjectId", String::class.java)
        )
    }
}