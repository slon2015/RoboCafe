package com.robocafe.all.domain

import org.springframework.data.domain.AbstractAggregateRoot
import java.lang.Exception
import java.util.function.Predicate
import javax.persistence.*


data class ChatMemberId(val partyId: String, val personId: String)
inline class MessageId(val id: String)
inline class ChatId(val id: String)


/// Constructor must not contain inline classes in signature
@Embeddable
class ChatMember(memberId: ChatMemberId) {
    val chatMemberId
        get() = ChatMemberId(partyId, personId)
    val partyId = memberId.partyId
    val personId = memberId.personId
}

@Entity
class Message(@field:Id private val id: String, val text: String,
              @field:Embedded
              val author: ChatMember,
              @field:ManyToOne
              @field:JoinColumn(name = "chat_id")
              val chat: Chat) {
    val messageId
        get() = MessageId(id)
}

@Entity
class Chat(@field:Id val id: String,
           val name: String,
            @field:ElementCollection
            @field:CollectionTable(name ="chat_chat_members")
           val members: MutableSet<ChatMember> = HashSet()
) : AbstractAggregateRoot<Chat>() {
    private val chatId
        get() = ChatId(id)

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.ALL])
    val messages: MutableSet<Message> = HashSet()

    fun joinMemberToChat(member: ChatMemberId) {
        members.add(ChatMember(member))
        registerEvent(MemberJoinedToChat(id, member))
    }

    fun sendMessage(messageId: String, messageText: String, author: ChatMemberId) {
        val message = Message(messageId, messageText, ChatMember(author), this)
        messages.add(message)
        registerEvent(MessageSentToChat(id, author, message.messageId))
    }

    fun removeMemberFromChat(member: ChatMemberId) {
        members.removeIf { it.partyId == member.partyId && it.personId == member.personId }
        registerEvent(MemberRemovedFromChat(id, member))
    }

    companion object {
        fun startChat(id: String, name: String, ids: MutableSet<ChatMemberId> = HashSet()): Chat {
            val members = ids.map { ChatMember(it) }.toMutableSet()
            val chat = Chat(id, name, members)
            chat.registerEvent(ChatStarted(chat.chatId, name, members))
            return chat
        }
    }
}