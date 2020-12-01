package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.OrderRepository
import com.robocafe.all.domain.Order
import com.robocafe.all.domain.OrderPosition
import com.robocafe.all.domain.OrderPositionData
import com.robocafe.all.domain.OrderStatus
import com.robocafe.all.menu.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class OrderAuthorData(
        val partyId: String,
        val memberId: String?
)

@Service
class OrderService @Autowired constructor(
        private val orderRepository: OrderRepository,
        private val partyService: PartyService,
        private val personService: PersonService,
        private val positionService: PositionService
) {

    /**
       Each service must contain private companion object Dsl with specific
       for this service domain language implemented in expansion functions.
       Most of this functions contains assertions. All exception throwings must be performed in this functions.
       Naming of this functions must contain "assert" and checked condition.
       Use infix functions everywhere you can.
       Most of implemented functions must be used in closure, created by function [operate]
    */
    private companion object Dsl {
        infix fun Order.operate(operation: Order.() -> Unit) {
            operation(this)
        }

        fun Order.assertPositionContainsInOrder(positionId: String): OrderPosition {
            return positions.find { it.id == positionId } ?: throw PositionNotInOrder()
        }

        fun Order.assertOrderIsNotClosed() {
            if (closed) {
                throw OrderAlreadyClosed()
            }
        }

        infix fun OrderPosition.assertPositionStatusEquals(orderStatus: OrderStatus) {
            if (orderStatus != orderStatus) {
                throw IncorrectPositionStatus()
            }
        }

        infix fun OrderAuthorData.checkPartyIn(partyService: PartyService): OrderAuthorData {
            if (!partyService.notEndedPartyExists(partyId)) {
                throw InvalidParty()
            }
            return this
        }

        infix fun OrderAuthorData.checkMemberIn(personService: PersonService): OrderAuthorData {
            if (memberId != null && !personService.personWithActivePartyExists(memberId, partyId)) {
                throw InvalidPerson()
            }
            return this
        }

        infix fun Set<OrderPositionData>.calculatePriceWith(positionService: PositionService): Double {
            return map { it to positionService.getPositionInfo(it.menuPositionId) }
                    .map { it.second.price * it.first.count }.sum()
        }
    }

    private fun findOrder(orderId: String) =
            orderRepository.findById(orderId).orElseThrow { OrderNotFound() }


    private fun Order.saveChanges() {
        orderRepository.save(this)
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>) {
        orderAuthorData checkPartyIn(partyService) checkMemberIn(personService)
        Order(
            id,
            orderAuthorData.partyId,
            orderAuthorData.memberId,
            positions, positions calculatePriceWith positionService
        ).saveChanges()
    }

    fun getBalanceForParty(partyId: String) =
            orderRepository.findAllByPartyIdAndClosedIsFalse(partyId)
                    .map { it.price }.sum()

    fun getBalanceForPerson(personId: String) =
            orderRepository.findAllByPersonIdAndClosedIsFalse(personId)
                    .map { it.price }.sum()



    fun changeOrderPrice(orderId: String, newPrice: Double) {
        findOrder(orderId) operate {
            assertOrderIsNotClosed()
            changePrice(newPrice)
            saveChanges()
        }
    }

    fun removeOrder(orderId: String) {
        findOrder(orderId) operate {
            assertOrderIsNotClosed()
            removeOrder()
            saveChanges()
        }
    }

    fun startPositionPreparing(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.WAITING
            startPositionPreparing(positionId)
            saveChanges()
        }
    }

    fun finishPositionPreparing(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.PREPARING
            preparePosition(positionId)
            saveChanges()
        }
    }

    fun finishPositionDelivering(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.DELIVERING
            deliverPosition(positionId)
            saveChanges()
        }
    }
}

