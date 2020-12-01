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
        private val positionRepository: PositionService
) {

    private companion object OrderDsl {
        infix fun Order.operate(operation: Order.() -> Unit) {
            operation(this)
        }

        fun Order.assertPositionInOrder(positionId: String): OrderPosition {
            return positions.find { it.id == positionId } ?: throw PositionNotInOrder()
        }

        fun Order.assertClosed() {
            if (closed) {
                throw OrderAlreadyClosed()
            }
        }

        infix fun OrderPosition.assertPositionStatus(orderStatus: OrderStatus) {
            if (orderStatus != orderStatus) {
                throw IncorrectPositionStatus()
            }
        }
    }

    private fun findOrder(orderId: String) =
            orderRepository.findById(orderId).orElseThrow { OrderNotFound() }


    private fun Order.save() {
        orderRepository.save(this)
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>) {
        when {
            !partyService.notEndedPartyExists(orderAuthorData.partyId) -> throw InvalidParty()
            orderAuthorData.memberId != null &&
                    !personService.personWithActivePartyExists(orderAuthorData.memberId,
                            orderAuthorData.partyId) ->
                throw InvalidPerson()
        }
        val price = positions.map { it to positionRepository.getPositionInfo(it.menuPositionId) }
                .map { it.second.price * it.first.count }.sum()
        Order(
            id,
            orderAuthorData.partyId,
            orderAuthorData.memberId,
            positions, price
        ).save()
    }

    fun getBalanceForParty(partyId: String) =
            orderRepository.findAllByPartyIdAndClosedIsFalse(partyId)
                    .map { it.price }.sum()

    fun getBalanceForPerson(personId: String) =
            orderRepository.findAllByPersonIdAndClosedIsFalse(personId)
                    .map { it.price }.sum()



    fun changeOrderPrice(orderId: String, newPrice: Double) {
        findOrder(orderId) operate {
            assertClosed()
            changePrice(newPrice)
            save()
        }
    }

    fun removeOrder(orderId: String) {
        findOrder(orderId) operate {
            assertClosed()
            removeOrder()
            save()
        }
    }

    fun startPositionPreparing(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertClosed()
            assertPositionInOrder(positionId) assertPositionStatus OrderStatus.WAITING
            startPositionPreparing(positionId)
            save()
        }
    }

    fun finishPositionPreparing(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertClosed()
            assertPositionInOrder(positionId) assertPositionStatus OrderStatus.PREPARING
            preparePosition(positionId)
            save()
        }
    }

    fun finishPositionDelivering(orderId: String, positionId: String) {
        findOrder(orderId) operate {
            assertClosed()
            assertPositionInOrder(positionId) assertPositionStatus OrderStatus.DELIVERING
            deliverPosition(positionId)
            save()
        }
    }
}

