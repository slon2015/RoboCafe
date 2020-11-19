package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.*
import com.robocafe.all.application.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/chats")
class ChatController @Autowired constructor(
        private val chatService: ChatService
) {

    private fun mapMemberId(type: IdType, id: String): MemberId {
        return when (type) {
            IdType.PARTY -> PartyMemberId(id)
            IdType.PERSON -> PersonMemberId(id)
        }
    }

    @PostMapping
    fun startChat(@RequestBody body: StartChatModel): ResponseEntity<ChatInfo> {
        return try {
            val chatId = UUID.randomUUID().toString()
            val chat = chatService.startChat(chatId,
                    body.members.entries.map { mapMemberId(it.key, it.value) }.toSet())
            ResponseEntity.ok(ChatInfo(chat))
        }
        catch (e: EntityNotFound) {
            ResponseEntity.notFound().build()
        }
        catch (e: DomainException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/{chatId}/members")
    fun addMember(@PathVariable chatId: String, @RequestBody body: AddMemberModel): ResponseEntity<String> {
        return try {
            val uniqueMemberId = UUID.randomUUID().toString()
            chatService.addMemberToChat(uniqueMemberId, chatId, mapMemberId(body.idType, body.id))
            ResponseEntity.ok(uniqueMemberId)
        }
        catch (e: EntityNotFound) {
            ResponseEntity.notFound().build()
        }
        catch (e: MemberAlreadyInChat) {
            ResponseEntity.status(HttpStatus.ACCEPTED).build()
        }
        catch (e: DomainException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/{chatId}/messages")
    fun sendMessage(@PathVariable chatId: String, @RequestBody body: SendMessageModel): ResponseEntity<String> {
        return try {
            val messageId = UUID.randomUUID().toString()
            chatService.sendMessage(messageId, chatId, body.memberId, body.text)
            ResponseEntity.ok(messageId)
        }
        catch (e: MemberNotInChat) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        catch (e: EntityNotFound) {
            ResponseEntity.notFound().build()
        }
        catch (e: DomainException) {
            ResponseEntity.badRequest().build()
        }
    }
}