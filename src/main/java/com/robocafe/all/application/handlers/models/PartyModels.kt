package com.robocafe.all.application.handlers.models

data class StartPaymentModel(
        val personId: String?,
        val amount: Double
)

data class AddPerson(
        val place: Int
)

data class AddPersonResponse(
        val id: String,
        val token: String
)