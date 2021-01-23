package com.robocafe.all.domain

import com.robocafe.all.domain.models.DetalizedChatMemberInfo
import com.robocafe.all.domain.models.OutboundMember
import com.robocafe.all.events.dispatching.SendToChat
import com.robocafe.all.events.dispatching.SendToHall

@SendToHall("/chats/start")
data class ChatStarted(
        val chatId: String,
        val name: String,
        val members: Set<DetalizedChatMemberInfo>
): DomainEvent {
    override fun convertForHall(): Any {
        return mapOf(
                "chatId" to chatId,
                "name" to name,
                "members" to members.map { OutboundMember(it.tableNum, it.placeNum) }
        )
    }
}

@SendToChat("/members/add")
data class MemberJoinedToChat(
        override val chatId: String,
        val member: DetalizedChatMemberInfo
): ChatDomainEvent {
    override fun convertForChat(): Any {
        return mapOf(
                "member" to OutboundMember(member.tableNum, member.placeNum)
        )
    }
}
@SendToChat("/message")
data class MessageSentToChat(
        override val chatId: String,
        val author: DetalizedChatMemberInfo,
        val messageId: MessageId,
        val text: String
): ChatDomainEvent {
    override fun convertForChat(): Any {
        return mapOf(
                "author" to OutboundMember(author.tableNum, author.placeNum),
                "text" to text
        )
    }
}
@SendToChat("/members/remove")
data class MemberRemovedFromChat(
        override val chatId: String,
        val member: DetalizedChatMemberInfo
): ChatDomainEvent {
    override fun convertForChat(): Any {
        return mapOf(
                "member" to OutboundMember(member.tableNum, member.placeNum)
        )
    }
}