package com.robocafe.all.application.handlers.models

import com.robocafe.all.application.services.ChatInfo
import com.robocafe.all.domain.ChatMemberId

data class StartChatModel(val members: Set<String>)
class ChatInfo(chat: ChatInfo, val myId: ChatMemberId) {
    val id = chat.id
    val members = chat.members.map { it.personId }.toSet()
}
data class AddMemberModel(val id: String)
data class SendMessageModel(val text: String)