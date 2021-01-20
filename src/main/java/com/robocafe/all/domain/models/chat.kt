package com.robocafe.all.domain.models

import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.Message

data class ChatMemberInfo(val partyId: String, val personId: String)
data class DetalizedChatMemberInfo(
        val partyId: String,
        val personId: String,
        val tableNum: Int,
        val placeNum: Int
) {
    fun toChatMemberInfo() = ChatMemberInfo(partyId, personId)
}

data class OutboundMember(
        val tableNum: Int,
        val placeNum: Int
)

data class MessageInfo(
        val id: String, val text: String,
        val author: ChatMemberInfo
) {
    constructor(data: Message): this(data.messageId.id, data.text, data.author.chatMemberId)
}

data class ChatInfo(
        val id: String,
        val name: String,
        val members: Set<ChatMemberInfo>,
        val messages: List<MessageInfo>
) {
    constructor(data: Chat): this(data.id, data.name, data.members.map { it.chatMemberId }.toSet(),
            data.messages.map { MessageInfo(it) }
    )
}