//package com.robocafe.all.application.handlers
//
//import com.robocafe.all.application.handlers.models.PartyInfo
//import com.robocafe.all.application.handlers.models.PartyStartModel
//import com.robocafe.all.application.services.*
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import java.util.*
//
//@RestController
//@RequestMapping("/parties")
//class PartyController @Autowired constructor(private val partyService: PartyService) {
//
//    @PostMapping
//    fun startParty(@RequestBody body: PartyStartModel): ResponseEntity<PartyInfo> {
//        val partyId = UUID.randomUUID().toString()
//        return try {
//            val party = partyService.startParty(body.tableId, partyId, body.membersCount)
//            ResponseEntity.ok(PartyInfo(party))
//        }
//        catch (e: TableNotFound) {
//            ResponseEntity.notFound().build()
//        }
//        catch (e: TableNotFree) {
//            ResponseEntity.status(HttpStatus.CONFLICT).build()
//        }
//        catch (e: TablePersonsCountLowerThanPartyMembersCount) {
//            ResponseEntity.badRequest().build()
//        }
//    }
//
//    @PostMapping("/{partyId}/person")
//    fun addMemberToParty(@PathVariable partyId: String): ResponseEntity<String> {
//        return try {
//            val personId = UUID.randomUUID().toString()
//            partyService.joinPerson(partyId, personId)
//            ResponseEntity.ok(personId)
//        }
//        catch (e: PartyNotFound) {
//            ResponseEntity.notFound().build()
//        }
//        catch (e: PartyAlreadyEnded) {
//            ResponseEntity.badRequest().build()
//        }
//        catch (e: PartyAlreadyFull) {
//            ResponseEntity.badRequest().build()
//        }
//    }
//
//}