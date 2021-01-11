package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.*
import com.robocafe.all.application.security.SecurityService
import com.robocafe.all.application.utils.ChatUtils
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
        private val securityService: SecurityService,
        private val chatUtils: ChatUtils
) {

    @PostMapping("/session")
    fun startSession(@RequestBody body: StartSessionModel, authentication: Authentication): StartSessionResponse {
        val partyId = UUID.randomUUID().toString()
        val tableId = authentication.name
        val party = sessionService.startSession(tableId, partyId, body.personCount)
        return StartSessionResponse(
                securityService.registerParty(party.id),
                party.members
                        .sortedBy { it.place }
                        .map { it.id to securityService.registerPerson(it.id) }.toList()
        )
    }

    @DeleteMapping("/session")
    fun endSession(authentication: Authentication) {
        val tableId = authentication.name
        sessionService.endSessionForTable(tableId)
    }

    @GetMapping("/init")
    fun initTable(authentication: Authentication): TableInitInfo {
        val data = sessionService.getInitDataForTable(authentication.name)
        return TableInitInfo(
                data.tableId,
                if (data.partyId != null)
                    SessionInfo(
                            data.partyId,
                            data.persons
                                    !!.map { personId ->
                                PersonInfo(personId,
                                    sessionService.getPlaceForPerson(personId),
                                    securityService.findTokenFor(personId, SecurityService.PERSON_ROLE),
                                    sessionService.getUnpayedBalanceForPerson(personId),
                                    sessionService.getOpenOrdersForPerson(personId),
                                    sessionService.getChatsForPerson(personId)
                                            .map { chatInfo -> ChatInitInfo(
                                                    OutboundChatInfo(chatInfo.id,
                                                            chatUtils.mapToOutboundMember(
                                                                    chatInfo.members.first { it.personId == personId}),
                                                            chatInfo.members.map(chatUtils::mapToOutboundMember).toSet()
                                                    ),
                                                    chatInfo.messages.map(chatUtils::mapToOutboundMessageInfo).toList())
                                            }.toSet()
                                        )
                                    }.toList()
                    ) else null,
                data.tableStatus,
                sessionService.getHallState()
        )
    }
}