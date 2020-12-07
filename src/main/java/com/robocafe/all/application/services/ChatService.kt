package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.ChatMembersRepository
import com.robocafe.all.application.repositories.ChatRepository
import com.robocafe.all.application.repositories.MessageRepository
import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.ChatMember
import com.robocafe.all.domain.ChatMemberType
import com.robocafe.all.domain.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

sealed class MemberId(val id: String)
class PersonMemberId(id: String): MemberId(id)
class PartyMemberId(id: String): MemberId(id)

data class ChatMemberInfo(
        val id: String,
        val partyId: String?,
        val personId: String?,
        val type: ChatMemberType
) {
    constructor(data: ChatMember): this(data.chatMemberId.id, data.partyId, data.personId, data.type)
}

data class MessageInfo(
        val id: String, val text: String,
        val author: String
) {
    constructor(data: Message): this(data.messageId.id, data.text, data.author.chatMemberId.id)
}

data class ChatInfo(
        val id: String,
        val members: Set<ChatMemberInfo>,
        val messages: Set<MessageInfo>
) {
    constructor(data: Chat): this(data.id, data.members.map { ChatMemberInfo(it) }.toSet(),
            data.messages.map { MessageInfo(it) }.toSet()
    )
}

@Service
class ChatService @Autowired constructor(
        private val chatRepository: ChatRepository,
        private val chatMembersRepository: ChatMembersRepository,
        private val messageRepository: MessageRepository,
) {

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    private fun mapMember(member: ChatMemberInfo): ChatMember {
        val mappedMember = ChatMember(member.id)
        when (member.type) {
            ChatMemberType.PARTY -> mappedMember.partyId = member.partyId
            ChatMemberType.PERSON -> mappedMember.personId = member.personId
        }
        return mappedMember
    }

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun startChat(chatId: String, members: Set<ChatMemberInfo>): ChatInfo {
        val mappedMembers = members.map { mapMember(it) }.toMutableSet()
        val chat = Chat.startChat(chatId, mappedMembers)
        chatRepository.save(chat)
        return ChatInfo(chat)
    }

    private fun getChat(chatId: String): Chat {
        return chatRepository.findById(chatId).orElseThrow { ChatNotFound() }
    }

    @Throws(ChatNotFound::class)
    fun findChat(chatId: String): ChatInfo {
        return ChatInfo(getChat(chatId))
    }

    private fun getChatMember(chatMemberId: String): ChatMember {
        return chatMembersRepository.findById(chatMemberId).orElseThrow { ChatMemberNotFound() }
    }

    @Throws(ChatMemberNotFound::class)
    fun findChatMember(chatMemberId: String): ChatMemberInfo {
        return ChatMemberInfo(getChatMember(chatMemberId))
    }

    @Throws(ChatNotFound::class, MemberAlreadyInChat::class,
            PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun addMemberToChat(chatId: String, member: ChatMemberInfo) {
        val mappedMember = mapMember(member)
        val chat = getChat(chatId)
        chat.joinMemberToChat(mappedMember)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class)
    fun removeMemberFromChat(chatId: String, memberId: String) {
        val chat = getChat(chatId)
        chat.removeMemberFromChat(memberId)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class, ChatMemberNotFound::class)
    fun sendMessage(messageId: String, chatId: String, memberId: String, text: String) {
        val chat = getChat(chatId)
        if (!chat.members.all { it.chatMemberId.id != memberId }) {
            throw MemberNotInChat()
        }
        val member = getChatMember(memberId)
        chat.sendMessage(messageId, text, member)
        chatRepository.save(chat)
    }
}