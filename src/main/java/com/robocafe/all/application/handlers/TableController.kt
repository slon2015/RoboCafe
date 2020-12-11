package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.StartSessionModel
import com.robocafe.all.application.handlers.models.StartSessionResponse
import com.robocafe.all.application.security.SecurityService
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tables")
@Transactional
class TableController @Autowired constructor(
        private val sessionService: SessionService,
        private val securityService: SecurityService
) {

    @PostMapping("/session")
    fun startSession(@RequestBody body: StartSessionModel, authentication: Authentication): StartSessionResponse {
        val partyId = UUID.randomUUID().toString()
        val tableId = authentication.name
        val party = sessionService.startSession(tableId, partyId, body.personCount)
        return StartSessionResponse(
                securityService.registerParty(party.id),
                party.members.map { it.id to securityService.registerPerson(it.id) }.toMap()
        )
    }

    @DeleteMapping("session")
    fun endSession(authentication: Authentication) {
        val tableId = authentication.name
        sessionService.endSessionForTable(tableId)
    }


}