package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.OrderPositionRepository
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

data class OrderPositionInfo(
        val id: String,
        val menuPositionId: String,
        var orderStatus: OrderStatus = OrderStatus.WAITING
) {
    constructor(data: OrderPosition): this(data.id, data.menuPositionId, data.orderStatus)
}

data class OrderInfo(
        val id: String, val partyId: String, val personId: String?,
        val positions: Set<OrderPositionInfo>,
        var price: Double
) {
    constructor(data: Order): this(data.id, data.partyId, data.personId,
            data.positions.map { OrderPositionInfo(it) }.toSet(), data.price)
}

@Service
class OrderService @Autowired constructor(
        private val orderRepository: OrderRepository,
        private val orderPositionRepository: OrderPositionRepository
) {

    /**
       Each service must contain private companion object Dsl with specific
       for this service domain language implemented in private expansion functions.
       Most of this functions contains assertions. All exception throwings must be performed in this functions.
       Naming of this functions must contain "assert" and checked condition.
       Use infix functions everywhere you can.
       Most of implemented functions must be used in closure, created by function [operate]
    */
    private companion object Dsl {
        private infix fun Order.operate(operation: Order.() -> Unit) {
            operation(this)
        }

        private fun Order.assertPositionContainsInOrder(positionId: String): OrderPosition {
            return positions.find { it.id == positionId } ?: throw PositionNotInOrder()
        }

        private fun Order.assertOrderIsNotClosed() {
            if (closed) {
                throw OrderAlreadyClosed()
            }
        }

        private infix fun OrderPosition.assertPositionStatusEquals(orderStatus: OrderStatus) {
            if (orderStatus != orderStatus) {
                throw IncorrectPositionStatus()
            }
        }
    }

    private fun getOrder(orderId: String) =
            orderRepository.findById(orderId).orElseThrow { OrderNotFound() }


    private fun Order.saveChanges() {
        orderRepository.save(this)
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>, price: Double) {
        Order.createOrder(
            id,
            orderAuthorData.partyId,
            orderAuthorData.memberId,
            positions, price
        ).saveChanges()
    }

    fun getBalanceForParty(partyId: String) =
            orderRepository.findAllByPartyId(partyId)
                    .map { it.price }.sum()

    fun getBalanceForPerson(personId: String) =
            orderRepository.findAllByPersonId(personId)
                    .map { it.price }.sum()



    fun changeOrderPrice(orderId: String, newPrice: Double) {
        getOrder(orderId) operate {
            assertOrderIsNotClosed()
            changePrice(newPrice)
            saveChanges()
        }
    }

    fun removeOrder(orderId: String) {
        getOrder(orderId) operate {
            assertOrderIsNotClosed()
            removeOrder()
            saveChanges()
        }
    }

    fun startPositionPreparing(orderId: String, positionId: String) {
        getOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.WAITING
            startPositionPreparing(positionId)
            saveChanges()
        }
    }

    fun finishPositionPreparing(orderId: String, positionId: String) {
        getOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.PREPARING
            preparePosition(positionId)
            saveChanges()
        }
    }

    fun finishPositionDelivering(orderId: String, positionId: String) {
        getOrder(orderId) operate {
            assertOrderIsNotClosed()
            assertPositionContainsInOrder(positionId) assertPositionStatusEquals OrderStatus.DELIVERING
            deliverPosition(positionId)
            saveChanges()
        }
    }

    fun getOpenOrders() = orderRepository.findAllByClosedIsFalse().map { OrderInfo(it) }.toSet()
    fun getPositionsForOrderThatWaitsForPreparing(orderId: String) =
            orderPositionRepository.findAllByOrderIdAndOrderStatusEqualsWaiting(orderId)
                    .map { OrderPositionInfo(it) }.toSet()
    fun getPositionsOnPreparingStage() =
            orderPositionRepository.findAllByOrderStatusEqualsPreparing()
                    .map { OrderPositionInfo(it) }.toSet()
    fun getPositionsOnDeliveringStage() =
            orderPositionRepository.findAllByOrderStatusEqualsDelivering()
                    .map { OrderPositionInfo(it) }.toSet()
    fun getOpenOrdersForParty(partyId: String): Set<OrderInfo> =
            orderRepository.findAllByPartyIdAndClosedIsFalse(partyId)
                    .map { OrderInfo(it) }.toSet()
    fun getOpenOrdersForPerson(personId: String) =
            orderRepository.findAllByPersonIdAndClosedIsFalse(personId)
                    .map { OrderInfo(it) }.toSet()

}

