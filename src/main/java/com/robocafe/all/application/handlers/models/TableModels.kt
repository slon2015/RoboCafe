package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.TableStatus

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
        val personTokens: Map<String, String>
)

data class PersonInfo(
        val id: String,
        val jwtToken: String,
        val balance: Double
)

data class SessionInfo(
        val partyToken: String,
        val persons: List<PersonInfo>
)


data class TableInitInfo(
        val tableId: String,
        val session: SessionInfo?,
        val tableStatus: TableStatus
)