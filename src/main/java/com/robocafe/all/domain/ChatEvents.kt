package com.robocafe.all.domain

import com.robocafe.all.events.dispatching.SendToChat

data class ChatStarted(
        val chatId: ChatId,
        val name: String,
        val members: MutableSet<ChatMember>
): DomainEvent

@SendToChat("/members/add")
data class MemberJoinedToChat(
        override val chatId: String,
        val memberId: ChatMemberId
): ChatDomainEvent
@SendToChat("/message")
data class MessageSentToChat(
        override val chatId: String,
        val authorId: ChatMemberId,
        val messageId: MessageId,
        val text: String
): ChatDomainEvent
data class MemberRemovedFromChat(
        override val chatId: String,
        val memberPartyId: ChatMemberId
): ChatDomainEvent