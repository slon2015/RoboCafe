package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.ChatMembersRepository
import com.robocafe.all.application.repositories.ChatRepository
import com.robocafe.all.application.repositories.MessageRepository
import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.ChatMember
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

sealed class MemberId(val id: String)
class PersonMemberId(id: String): MemberId(id)
class PartyMemberId(id: String): MemberId(id)

@Service
class ChatService @Autowired constructor(
        private val chatRepository: ChatRepository,
        private val chatMembersRepository: ChatMembersRepository,
        private val messageRepository: MessageRepository,
        private val partyService: PartyService,
        private val personService: PersonService
) {

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    private fun mapMember(uniqueMemberId: String, it: MemberId): ChatMember {
        val member = ChatMember(uniqueMemberId)
        when (it) {
            is PersonMemberId -> {
                val person = personService.findPerson(it.id)
                if (person.party.isEnded()) {
                    throw PersonsPartyEnded()
                }
                member.personId = person.id
            }
            is PartyMemberId -> {
                val party = partyService.findNotEndedParty(it.id)
                member.partyId = party.id
            }
        }
        return member
    }

    @Throws(PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun startChat(chatId: String, memberIds: Set<MemberId>): Chat {
        val members = memberIds.map { mapMember(UUID.randomUUID().toString(), it) }.toMutableSet()
        val chat = Chat(chatId, members)
        chatRepository.save(chat)
        return chat
    }

    @Throws(ChatNotFound::class)
    private fun findChat(chatId: String): Chat {
        return chatRepository.findById(chatId).orElseThrow { ChatNotFound() }
    }

    @Throws(ChatMemberNotFound::class)
    private fun findChatMember(chatMemberId: String): ChatMember {
        return chatMembersRepository.findById(chatMemberId).orElseThrow { ChatMemberNotFound() }
    }

    @Throws(ChatNotFound::class, MemberAlreadyInChat::class,
            PersonNotFound::class, PersonsPartyEnded::class, PartyNotFound::class, PartyAlreadyEnded::class)
    fun addMemberToChat(uniqueMemberId: String, chatId: String, idObject: MemberId) {
        val member = mapMember(uniqueMemberId, idObject)
        val chat = findChat(chatId)
        if (chat.members.contains(member)) {
            throw MemberAlreadyInChat()
        }
        chat.joinMemberToChat(member)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class)
    fun removeMemberFromChat(chatId: String, memberId: String) {
        val chat = findChat(chatId)
        if (!chat.members.all { it.chatMemberId.id != memberId }) {
            throw MemberNotInChat()
        }
        chat.removeMemberFromChat(memberId)
        chatRepository.save(chat)
    }

    @Throws(ChatNotFound::class, MemberNotInChat::class, ChatMemberNotFound::class)
    fun sendMessage(messageId: String, chatId: String, memberId: String, text: String) {
        val chat = findChat(chatId)
        if (!chat.members.all { it.chatMemberId.id != memberId }) {
            throw MemberNotInChat()
        }
        val member = findChatMember(memberId)
        chat.sendMessage(messageId, text, member)
        chatRepository.save(chat)
    }
}