package com.robocafe.all.application.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.robocafe.all.application.services.ChatInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class HallBroadcastService @Autowired constructor(
        private val messagingTemplate: SimpMessagingTemplate,
        private val broadcastPrefix: String = "/hall/broadcast"
) {

    fun broadcast(topic: String, event: Any) {
        messagingTemplate.convertAndSend("${broadcastPrefix}/${topic}", event)
    }

    fun broadcastToChat(topic: String, chatInfo: ChatInfo, event: Any) {
        messagingTemplate.convertAndSend("${broadcastPrefix}/chats/${chatInfo.id}/${topic}", event)
    }
}