package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.AddPerson
import com.robocafe.all.application.handlers.models.AddPersonResponse
import com.robocafe.all.application.handlers.models.StartPaymentModel
import com.robocafe.all.application.security.SecurityService
import com.robocafe.all.application.services.PaymentInfo
import com.robocafe.all.application.services.PaymentTarget
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/parties")
class PartyController @Autowired constructor(
        private val sessionService: SessionService,
        private val securityService: SecurityService
) {

    @PostMapping("/members")
    fun addMember(@RequestBody body: AddPerson, authentication: Authentication): AddPersonResponse {
        val partyId = authentication.name
        val personId = UUID.randomUUID().toString()
        sessionService.joinPerson(partyId, personId, body.place)
        return AddPersonResponse(
                personId,
                securityService.registerPerson(personId)
        )
    }

    @PostMapping("/payment")
    fun startPayment(@RequestBody body: StartPaymentModel, authentication: Authentication): PaymentInfo {
        val partyId = authentication.name
        return sessionService.createPayment(
                PaymentTarget(partyId, body.personId), body.amount
        )
    }

}