package com.robocafe.core.domain

import com.robocafe.core.domain.models.ChatMemberInfo
import com.robocafe.core.domain.models.DetalizedChatMemberInfo
import org.springframework.data.domain.AbstractAggregateRoot
import java.lang.Exception
import java.util.function.Predicate
import javax.persistence.*


inline class MessageId(val id: String)
inline class ChatId(val id: String)


/// Constructor must not contain inline classes in signature
@Embeddable
class ChatMember(val partyId: String, val personId: String) {
    constructor(data: ChatMemberInfo): this(data.partyId, data.personId)
    constructor(data: DetalizedChatMemberInfo): this(data.partyId, data.personId)
    val chatMemberId
        get() = ChatMemberInfo(partyId, personId)
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

    fun joinMemberToChat(member: DetalizedChatMemberInfo) {
        members.add(ChatMember(member))
        registerEvent(MemberJoinedToChat(id, member))
    }

    fun sendMessage(messageId: String, messageText: String, author: DetalizedChatMemberInfo) {
        val message = Message(messageId, messageText, ChatMember(author), this)
        messages.add(message)
        registerEvent(MessageSentToChat(id, author, message.messageId, messageText))
    }

    fun removeMemberFromChat(member: DetalizedChatMemberInfo) {
        members.removeIf { it.partyId == member.partyId && it.personId == member.personId }
        registerEvent(MemberRemovedFromChat(id, member))
    }

    companion object {
        fun startChat(id: String, name: String, ids: Set<DetalizedChatMemberInfo> = HashSet()): Chat {
            val members = ids.map { ChatMember(it) }.toMutableSet()
            val chat = Chat(id, name, members)
            chat.registerEvent(ChatStarted(chat.chatId.id, name, ids))
            return chat
        }
    }
}