package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.Chat

enum class IdType {
    PERSON,
    PARTY
}
data class StartChatModel(val members: Map<IdType, String>)
class ChatInfo(chat: Chat) {
    val id = chat.id
    val members = chat.members.map { it.chatMemberId }.toSet()
}
data class AddMemberModel(val idType: IdType, val id: String)
data class SendMessageModel(val text: String, val memberId: String)