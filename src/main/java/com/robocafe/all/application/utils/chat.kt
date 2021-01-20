package com.robocafe.all.application.utils

import com.robocafe.all.application.handlers.models.OutboundMessageInfo
import com.robocafe.all.domain.models.ChatMemberInfo
import com.robocafe.all.domain.models.DetalizedChatMemberInfo
import com.robocafe.all.domain.models.MessageInfo
import com.robocafe.all.domain.models.OutboundMember
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChatUtils @Autowired constructor(
        private val sessionService: SessionService
) {
    fun mapToDomainMemberId(personId: String): DetalizedChatMemberInfo {
        val person = sessionService.getPerson(personId)
        val party = sessionService.getPartyForPerson(personId)
        val table = sessionService.getTableInfo(party.tableId)
        return DetalizedChatMemberInfo(party.id, person.id, table.tableNumber, person.place)
    }
    fun mapToDomainMemberId(member: OutboundMember): DetalizedChatMemberInfo {
        val table = sessionService.getTableByNumber(member.tableNum)
        val party = sessionService.getActivePartyForTable(table.id)
        val person = sessionService.getPersonFromPartyByPlace(party.id, member.placeNum)
        return DetalizedChatMemberInfo(party.id, person.id, table.tableNumber, person.place)
    }
    fun mapToOutboundMember(member: ChatMemberInfo): OutboundMember {
        val party = sessionService.getParty(member.partyId)
        val table = sessionService.getTableInfo(party.tableId)
        val person = sessionService.getPerson(member.personId)
        return OutboundMember(table.tableNumber, person.place)
    }
    fun mapToOutboundMessageInfo(data: MessageInfo): OutboundMessageInfo {
        return OutboundMessageInfo(data.id, data.text, mapToOutboundMember(data.author))
    }
}