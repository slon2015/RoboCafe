package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.ChatRepository
import com.robocafe.all.application.repositories.MessageRepository
import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.ChatMember
import com.robocafe.all.domain.Message
import com.robocafe.all.domain.models.ChatInfo
import com.robocafe.all.domain.models.ChatMemberInfo
import com.robocafe.all.domain.models.DetalizedChatMemberInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service




@Service
class ChatService @Autowired constructor(
        private val chatRepository: ChatRepository,
        private val messageRepository: MessageRepository,
) {

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun startChat(chatId: String, chatName: String, members: Set<DetalizedChatMemberInfo>): ChatInfo {
        val chat = Chat.startChat(chatId, chatName, members)
        chatRepository.save(chat)
        return ChatInfo(chat)
    }

    fun getChatsFor(chatMemberId: ChatMemberInfo): Set<ChatInfo> {
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
    fun addMemberToChat(chatId: String, member: DetalizedChatMemberInfo) {
        val chat = getChat(chatId)
        chat.joinMemberToChat(member)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class)
    fun removeMemberFromChat(chatId: String, chatMemberId: DetalizedChatMemberInfo) {
        val chat = getChat(chatId)
        chat.removeMemberFromChat(chatMemberId)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class, ChatMemberNotFound::class)
    fun sendMessage(
            messageId: String,
            chatId: String,
            memberId: DetalizedChatMemberInfo,
            text: String
    ) {
        val chat = getChat(chatId)
        chat.sendMessage(messageId, text, memberId)
        chatRepository.save(chat)
    }
}