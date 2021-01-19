package com.robocafe.all.application.websocket.stomp

import com.robocafe.all.application.security.JwtProvider
import com.robocafe.all.application.security.ObjectUserDetailsService
import com.robocafe.all.application.security.authenticateToken
import io.jsonwebtoken.JwtException
import org.apache.logging.log4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class StompAuthInterceptor @Autowired constructor(
        private val jwtProvider: JwtProvider,
        private val objectUserDetailsService: ObjectUserDetailsService,
): ChannelInterceptor {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun postReceive(message: Message<*>, channel: MessageChannel): Message<*>? {
        val token = message.headers.get("authorization", String::class.java)
        if (token != null) {
            try {
                SecurityContextHolder.getContext().authentication =
                        authenticateToken(token, jwtProvider, objectUserDetailsService)
            }
            catch (e: JwtException) {
                logger.warn("JWT decoding failed", e)
            }
            return message
        }
        return null
    }
}