package com.robocafe.all.application.security

import io.jsonwebtoken.JwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

fun convertObjectToUserDetails(securityObject: Object): ObjectUserDetails {
    return ObjectUserDetails(
            securityObject.id,
            securityObject.role!!.name,
            securityObject.domainId,
            setOf(securityObject.role!!.permission, securityObject.additionalPermissions).flatten()
                    .map { it.name },
            securityObject.invalidated
    )
}

class ObjectUserDetails(
        val securityObjectId: String,
        val role: String,
        private val domainId: String?,
        private val permissions: Collection<String>,
        private val invalidated: Boolean
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = permissions.map { SimpleGrantedAuthority(it) }.toMutableSet()
        authorities.add(SimpleGrantedAuthority("ROLE_$role"))
        return authorities
    }

    override fun getPassword(): String {
        throw NotImplementedError()
    }

    override fun getUsername() = domainId

    override fun isAccountNonExpired(): Boolean {
        throw NotImplementedError()
    }

    override fun isAccountNonLocked() = !invalidated

    override fun isCredentialsNonExpired(): Boolean {
        throw NotImplementedError()
    }

    override fun isEnabled(): Boolean {
        throw NotImplementedError()
    }
}

@Component
class ObjectUserDetailsService @Autowired constructor(
        private val objectRepository: ObjectRepository
): UserDetailsService {

    override fun loadUserByUsername(id: String): ObjectUserDetails {
        val securityObject = objectRepository.findById(id).orElseThrow { UsernameNotFoundException(id) }
        return convertObjectToUserDetails(securityObject)
    }

}

@Component
class JwtFilter @Autowired constructor(
        private val jwtProvider: JwtProvider,
        private val objectUserDetailsService: ObjectUserDetailsService
): GenericFilterBean() {

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization")
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val token = getTokenFromRequest(request as HttpServletRequest)
        if (token != null) {
            try {
                val securityObjectId = jwtProvider.getInfoFromToken(token).securityObjectId
                val userDetails = objectUserDetailsService.loadUserByUsername(securityObjectId)
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
            catch (e: JwtException) {
                logger.warn("JWT decoding failed", e)
            }
        }
        chain.doFilter(request, response)
    }

}

