package com.robocafe.all.domain

import org.springframework.data.domain.AbstractAggregateRoot
import java.lang.Exception
import java.util.function.Predicate
import javax.persistence.*

class ChatMemberAlreadyAssignedToPerson: Exception()
class ChatMemberAlreadyAssignedToParty: Exception()

inline class ChatMemberId(val id: String)
inline class MessageId(val id: String)
inline class ChatId(val id: String)

enum class ChatMemberType {
    PARTY,
    PERSON,
    UNKNOWN
}

@Entity
/// Constructor must not contain inline classes in signature
class ChatMember(@field:Id private val id: String) {
    val chatMemberId
        get() = ChatMemberId(id)
    var partyId: String? = null
        set(value) {
            if (personId != null) {
                throw ChatMemberAlreadyAssignedToPerson()
            }
            field = value
        }
    var personId: String? = null
        set(value) {
            if (partyId != null) {
                throw ChatMemberAlreadyAssignedToParty()
            }
            field = value
        }
    val type: ChatMemberType
        get() {
            return when {
                personId != null -> {
                    ChatMemberType.PERSON
                }
                partyId != null -> {
                    ChatMemberType.PARTY
                }
                else -> {
                    ChatMemberType.UNKNOWN
                }
            }
        }
}

@Entity
class Message(@field:Id private val id: String, val text: String,
                @field:ManyToOne @field:JoinColumn(name = "authorId")
              val author: ChatMember,
                @field:ManyToOne @field:JoinColumn(name = "chatId")
              val chat: Chat) {
    val messageId
        get() = MessageId(id)
}

@Entity
class Chat(@field:Id val id: String,
            @field:ManyToMany @field:JoinTable(name ="chat_chat_members")
           val members: MutableSet<ChatMember> = HashSet()
) : AbstractAggregateRoot<Chat?>() {
    private val chatId
        get() = ChatId(id)

    @OneToMany(mappedBy = "chat")
    val messages: MutableSet<Message> = HashSet()

    init {
        registerEvent(ChatStarted(chatId, members))
    }

    fun joinMemberToChat(member: ChatMember) {
        members.add(member)
        registerEvent(MemberJoinedToChat(chatId, member.chatMemberId))
    }

    fun joinPersonMemberToChat(memberId: String, personId: String) {
        val member = ChatMember(memberId)
        member.personId = personId
        joinMemberToChat(member)
    }

    fun joinPartyMemberToChat(memberId: String, partyId: String) {
        val member = ChatMember(memberId)
        member.partyId = partyId
        joinMemberToChat(member)
    }

    fun sendMessage(messageId: String, messageText: String, author: ChatMember) {
        val message = Message(messageId, messageText, author, this)
        messages.add(message)
        registerEvent(MessageSentToChat(chatId, author.chatMemberId, message.messageId))
    }

    fun removeMemberFromChat(memberId: String) {
        members.removeIf(Predicate.isEqual(memberId))
        registerEvent(MemberRemovedFromChat(chatId, ChatMemberId(memberId)))
    }
}