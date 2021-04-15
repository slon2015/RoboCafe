package com.robocafe.all.application.services

import com.robocafe.core.repositories.OrderPositionRepository
import com.robocafe.core.repositories.OrderRepository
import com.robocafe.core.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class OrderAuthorData(
        val partyId: String,
        val memberId: String
)

data class OrderPositionInfo(
        val id: String,
        val menuPositionId: String,
        var orderStatus: OrderStatus = OrderStatus.WAITING
) {
    constructor(data: OrderPosition): this(data.id, data.menuPositionId, data.orderStatus)
}

data class OrderInfo(
        val id: String, val partyId: String, val personId: String,
        val positions: Set<OrderPositionInfo>,
        var price: Double,
        val closed: Boolean,
        val closeCause: CloseCause?
) {
    constructor(data: Order): this(data.id, data.partyId, data.personId,
            data.positions.map { OrderPositionInfo(it) }.toSet(), data.price,
            data.closed,
            data.closeCause)
}

@Service
class OrderService @Autowired constructor(
        private val orderRepository: OrderRepository,
        private val orderPositionRepository: OrderPositionRepository
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

        infix fun OrderPosition.operate(operation: OrderPosition.() -> Unit) {
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

        fun Order.assertOrderNotCompleted() {
            if (positions.all { it.orderStatus == OrderStatus.COMPLETED }) {
                throw OrderAlreadyCompleted()
            }
        }

        infix fun OrderPosition.assertPositionStatusEquals(orderStatus: OrderStatus) {
            if (orderStatus != orderStatus) {
                throw IncorrectPositionStatus()
            }
        }

        fun OrderPosition.assertIsWaiting() {
            if (orderStatus != OrderStatus.WAITING) {
                throw IncorrectPositionStatus()
            }
        }
    }

    private fun findOrder(orderId: String): Order =
            orderRepository.findById(orderId).orElseThrow { OrderNotFound() }
    private fun findPosition(positionId: String): OrderPosition =
            orderPositionRepository.findById(positionId).orElseThrow { PositionNotFound() }

    fun getOrder(orderId: String) = OrderInfo(findOrder(orderId))


    private fun Order.saveChanges() {
        orderRepository.save(this)
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>, price: Double): OrderInfo {
        val result = Order.createOrder(
            id,
            orderAuthorData.partyId,
            orderAuthorData.memberId,
            positions, price
        )
        result.saveChanges()
        return OrderInfo(result)
    }

    fun getBalanceForParty(partyId: String) =
            orderRepository.findAllByPartyId(partyId)
                    .map<Order, Double> { it.price }.sum()

    fun getBalanceForPerson(personId: String) =
            orderRepository.findAllByPersonId(personId)
                    .map<Order, Double> { it.price }.sum()



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
            assertOrderNotCompleted()
            removeOrder()
            saveChanges()
        }
    }

    fun cancelOrderPosition(positionId: String, personId: String) {
        findPosition(positionId) operate {
            assertIsWaiting()
            order.cancelPosition(this, personId)
            order.saveChanges()
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

    fun getOpenOrders() = orderRepository.findAllByClosedIsFalse()
            .map<Order, OrderInfo> { OrderInfo(it) }.toSet()
    fun getPositionsForOrderThatWaitsForPreparing(orderId: String) =
            orderPositionRepository.findAllByOrderIdAndOrderStatusEqualsWaiting(orderId)
                    .map<OrderPosition, OrderPositionInfo> { OrderPositionInfo(it) }.toSet()
    fun getPositionsOnPreparingStage() =
            orderPositionRepository.findAllByOrderStatusEqualsPreparing()
                    .map<OrderPosition, OrderPositionInfo> { OrderPositionInfo(it) }.toSet()
    fun getPositionsOnDeliveringStage() =
            orderPositionRepository.findAllByOrderStatusEqualsDelivering()
                    .map<OrderPosition, OrderPositionInfo> { OrderPositionInfo(it) }.toSet()
    fun getOpenOrdersForParty(partyId: String): Set<OrderInfo> =
            orderRepository.findAllByPartyIdAndClosedIsFalse(partyId)
                    .map<Order, OrderInfo> { OrderInfo(it) }.toSet()
    fun getOpenOrdersForPerson(personId: String) =
            orderRepository.findAllByPersonIdAndClosedIsFalse(personId)
                    .map<Order, OrderInfo> { OrderInfo(it) }.toSet()

}


