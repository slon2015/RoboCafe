package com.robocafe.all.application.handlers.models

import com.robocafe.all.application.services.MessageInfo
import com.robocafe.all.application.services.OrderInfo
import com.robocafe.all.domain.TableStatus
import com.robocafe.all.hallscheme.HallState

data class RegisterTable(val tableNum: Int, val tableMaxPersons: Int)
data class TableRegistrationResponse(
        val tableId: String,
        val tableToken: String
)
data class TableId(val id: String)
data class StartSessionModel(
        val personCount: Int
)
data class StartSessionResponse(
        val partyToken: String,
        val personTokens: List<Pair<String, String>>
)

data class PersonInfo(
        val id: String,
        val place: Int,
        val jwtToken: String,
        val balance: Double,
        val orders: Set<OrderInfo>,
        val chats: Set<ChatInitInfo>
)

data class SessionInfo(
        val partyToken: String,
        val persons: List<PersonInfo>
)

data class ChatInitInfo(
        val chatInfo: OutboundChatInfo,
        val messages: List<OutboundMessageInfo>
)

data class HallStateInitInfo(
        val staticState: HallState,
        val tableStatuses: Map<Int, Set<Int>?>
)

data class TableInitInfo(
        val tableId: String,
        val session: SessionInfo?,
        val tableStatus: TableStatus,
        val hallState: HallStateInitInfo,
        val tableNum: Int
)
