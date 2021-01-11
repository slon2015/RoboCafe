package com.robocafe.all.application.handlers

import com.robocafe.all.application.UnauthorizedForThisChat
import com.robocafe.all.application.handlers.models.*
import com.robocafe.all.application.services.MessageInfo
import com.robocafe.all.application.services.OrderAuthorData
import com.robocafe.all.application.services.OrderInfo
import com.robocafe.all.application.utils.ChatUtils
import com.robocafe.all.domain.ChatMemberId
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/persons")
class PersonController @Autowired constructor(
        private val sessionService: SessionService,
        private val chatUtils: ChatUtils
) {

    @PostMapping("/order")
    fun makeOrder(@RequestBody body: MakeOrderModel, authentication: Authentication): OrderInfo {
        val personId = authentication.name
        val partyId = sessionService.getPartyForPerson(personId).id
        return sessionService.createOrder(OrderAuthorData(partyId, personId), body.positions)
    }

    @GetMapping("/view/balance")
    fun getPersonBalance(authentication: Authentication): Double {
        val personId = authentication.name
        return sessionService.getUnpayedBalanceForPerson(personId)
    }

    @GetMapping("/afiches/list")
    fun getAfichesList() = sessionService.getAfichesList()

    @GetMapping("/afiches/{aficheId}/content")
    fun getAficheContent(@PathVariable aficheId: String) = sessionService.getAficheContent(aficheId)

    @PostMapping("/chats")
    fun createChat(@RequestBody body: StartChatModel, authentication: Authentication): OutboundChatInfo {
        val chatId = UUID.randomUUID().toString()
        val personId = authentication.name
        val myChatId = chatUtils.mapToDomainMemberId(personId)
        val chat = sessionService.startChat(chatId, body.chatName,
                body.members.map { chatUtils.mapToDomainMemberId(it) }.plus(myChatId).toSet())
        return OutboundChatInfo(chat.id, chat.name,
                chatUtils.mapToOutboundMember(myChatId),
                chat.members.map(chatUtils::mapToOutboundMember).toSet())
    }

    private fun checkAuthorityForChat(myMemberId: ChatMemberId, chat: com.robocafe.all.application.services.ChatInfo) {
        if (!chat.members.contains(myMemberId)) {
            throw UnauthorizedForThisChat()
        }
    }

    @GetMapping("/chats/{chatId}/messages")
    fun getChatMessages(@PathVariable chatId: String, authentication: Authentication): List<OutboundMessageInfo> {
        val myMemberId = chatUtils.mapToDomainMemberId(authentication.name)
        val chat = sessionService.findChat(chatId)
        checkAuthorityForChat(myMemberId, chat)
        return chat.messages.map(this.chatUtils::mapToOutboundMessageInfo).toList()
    }

    @PostMapping("/chats/{chatId}/messages")
    fun sendMessage(@PathVariable chatId: String, authentication: Authentication, @RequestBody body: SendMessageModel) {
        val myMemberId = chatUtils.mapToDomainMemberId(authentication.name)
        val chat = sessionService.findChat(chatId)
        checkAuthorityForChat(myMemberId, chat)
        sessionService.sendMessage(UUID.randomUUID().toString(), chatId, myMemberId, body.text)
    }

    @PostMapping("/chats/{chatId}/members")
    fun addMember(@PathVariable chatId: String, authentication: Authentication, @RequestBody body: AddMemberModel) {
        val myMemberId = chatUtils.mapToDomainMemberId(authentication.name)
        val chat = sessionService.findChat(chatId)
        checkAuthorityForChat(myMemberId, chat)
        sessionService.addMemberToChat(chatId, chatUtils.mapToDomainMemberId(body.member))
    }
}