package com.robocafe.all.application.utils

import com.robocafe.all.application.handlers.models.OutboundMember
import com.robocafe.all.application.handlers.models.OutboundMessageInfo
import com.robocafe.all.application.services.MessageInfo
import com.robocafe.all.domain.ChatMemberId
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChatUtils @Autowired constructor(
        private val sessionService: SessionService
) {
    fun mapToDomainMemberId(personId: String) = ChatMemberId(sessionService.getPartyForPerson(personId).id, personId);
    fun mapToDomainMemberId(member: OutboundMember): ChatMemberId {
        val table = sessionService.getTableByNumber(member.tableNum)
        val party = sessionService.getActivePartyForTable(table.id)
        val person = sessionService.getPersonFromPartyByPlace(party.id, member.placeNum)
        return ChatMemberId(party.id, person.id)
    }
    fun mapToOutboundMember(member: ChatMemberId): OutboundMember {
        val party = sessionService.getParty(member.partyId)
        val table = sessionService.getTableInfo(party.tableId)
        val person = sessionService.getPerson(member.personId)
        return OutboundMember(table.tableNumber, person.place)
    }
    fun mapToOutboundMessageInfo(data: MessageInfo): OutboundMessageInfo {
        return OutboundMessageInfo(data.id, data.text, mapToOutboundMember(data.author))
    }
}