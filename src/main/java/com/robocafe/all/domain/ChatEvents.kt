package com.robocafe.all.domain

data class ChatStarted(val chatId: ChatId, val members: MutableSet<ChatMember>)
data class MemberJoinedToChat(val chatId: ChatId, val memberId: ChatMemberId)
data class MessageSentToChat(val chatId: ChatId, val authorId: ChatMemberId, val messageId: MessageId)
data class MemberRemovedFromChat(val chatId: ChatId, val memberId: ChatMemberId)