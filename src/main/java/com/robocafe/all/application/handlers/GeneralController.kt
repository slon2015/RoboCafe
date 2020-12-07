package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.PartyStartModel
import com.robocafe.all.application.services.OrderService
import com.robocafe.all.application.services.PartyService
import com.robocafe.all.application.services.TableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("/access-table")
@RestController
class TableHandlers @Autowired constructor(
        private val tableService: TableService,
        private val partyService: PartyService
) {

    @PostMapping("/party")
    fun createParty(@RequestBody body: PartyStartModel) {
        tableService.occupyTable(body.tableId)
        partyService.startParty(body.tableId, UUID.randomUUID().toString(), body.membersCount)
    }
}

@RequestMapping("/access-party")
@RestController
class PartyHandlers @Autowired constructor(
        private val tableService: TableService,
        private val partyService: PartyService
) {
    @PostMapping("/member/{partyId}")
    fun addMember(@PathVariable partyId: String) {
        partyService.joinPerson(partyId, UUID.randomUUID().toString())
    }
}

@RequestMapping("/access-table")
@RestController
class PersonHandlers @Autowired constructor(
        private val orderService: OrderService
) {

    @DeleteMapping("/exit")
    fun exitFromParty() {

    }
}
