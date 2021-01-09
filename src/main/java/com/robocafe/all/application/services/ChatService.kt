package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.ChatRepository
import com.robocafe.all.application.repositories.MessageRepository
import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.ChatMember
import com.robocafe.all.domain.ChatMemberId
import com.robocafe.all.domain.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


data class MessageInfo(
        val id: String, val text: String,
        val author: ChatMemberId
) {
    constructor(data: Message): this(data.messageId.id, data.text, data.author.chatMemberId)
}

data class ChatInfo(
        val id: String,
        val members: Set<ChatMemberId>,
        val messages: List<MessageInfo>
) {
    constructor(data: Chat): this(data.id, data.members.map { it.chatMemberId }.toSet(),
            data.messages.map { MessageInfo(it) }
    )
}

@Service
class ChatService @Autowired constructor(
        private val chatRepository: ChatRepository,
        private val messageRepository: MessageRepository,
) {

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun startChat(chatId: String, members: Set<ChatMemberId>): ChatInfo {
        val mappedMembers = members.map { ChatMemberId(it.partyId, it.personId) }.toMutableSet()
        val chat = Chat.startChat(chatId, mappedMembers)
        chatRepository.save(chat)
        return ChatInfo(chat)
    }

    fun getChatsFor(chatMemberId: ChatMemberId): Set<ChatInfo> {
        return chatRepository.findByMembersContains(ChatMember(chatMemberId)).map { ChatInfo(it) }.toSet()
    }

    private fun getChat(chatId: String): Chat {
        return chatRepository.findById(chatId).orElseThrow { ChatNotFound() }
    }

    @Throws(ChatNotFound::class)
    fun findChat(chatId: String): ChatInfo {
        return ChatInfo(getChat(chatId))
    }

    @Throws(ChatNotFound::class, MemberAlreadyInChat::class,
            PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun addMemberToChat(chatId: String, member: ChatMemberId) {
        val mappedMember = ChatMemberId(member.partyId, member.personId)
        val chat = getChat(chatId)
        chat.joinMemberToChat(mappedMember)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class)
    fun removeMemberFromChat(chatId: String, chatMemberId: ChatMemberId) {
        val chat = getChat(chatId)
        chat.removeMemberFromChat(chatMemberId)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class, ChatMemberNotFound::class)
    fun sendMessage(messageId: String, chatId: String, memberId: ChatMemberId, text: String) {
        val chat = getChat(chatId)
        chat.sendMessage(messageId, text, memberId)
        chatRepository.save(chat)
    }
}