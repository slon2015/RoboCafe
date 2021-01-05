package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.MakeOrderModel
import com.robocafe.all.application.services.OrderAuthorData
import com.robocafe.all.application.services.OrderInfo
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/persons")
class PersonController @Autowired constructor(
        private val sessionService: SessionService
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
}