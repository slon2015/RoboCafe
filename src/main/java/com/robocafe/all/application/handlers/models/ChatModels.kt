package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.models.OutboundMember

data class StartChatModel(val members: Set<OutboundMember>, val chatName: String)
class OutboundChatInfo(val chatId: String,
                       val name: String,
                       val myId: OutboundMember,
                       val members: Set<OutboundMember>)
data class AddMemberModel(val member: OutboundMember)
data class SendMessageModel(val text: String)
data class OutboundMessageInfo(val id: String, val text: String, val author: OutboundMember)