//package com.robocafe.all.application.services
//
//import com.robocafe.all.application.repositories.OrderRepository
//import com.robocafe.all.domain.Order
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//@Service
//class OrderService @Autowired constructor(
//        private val orderRepository: OrderRepository,
//        private val partyService: PartyService,
//        private val personService: PersonService
//) {
//
//     fun createOrder(id: String, partyId: String, personId: String?, positions: Set<String>) {
//          when {
//               !partyService.notEndedPartyExists(partyId) -> throw InvalidParty()
//               personId != null && !personService.personWithActivePartyExists(personId, partyId) ->
//                    throw InvalidPerson()
//               !positionRepository.existsAllById(positions) -> throw PositionNotFound()
//          }
//          orderRepository.save(Order(
//                  id, partyId, personId, positionRepository.findAllById(positions).toMutableSet()
//          ))
//     }
//
//     fun getBalanceForParty(partyId: String) =
//          orderRepository.findAllByPartyIdAndClosedIsFalse(partyId)
//                    .map { it.price }.sum()
//
//     fun getBalanceForPerson(personId: String) =
//             orderRepository.findAllByPersonIdAndClosedIsFalse(personId)
//                     .map { it.price }.sum()
//
//}