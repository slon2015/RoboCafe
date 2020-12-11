package com.robocafe.all.application.handlers.models

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