package com.robocafe.core.domain

import com.robocafe.core.events.dispatching.SendToPerson

data class OrderPositionPoint(val menuPositionId: String,
                              val positionId: String) {
    constructor(data: OrderPosition): this(data.menuPositionId, data.id)
}

data class OrderCreated(
        val id: String,
        val partyId: String,
        val personId: String,
        val positions: Set<OrderPositionPoint>): DomainEvent

data class OrderPriceChanged(
        val orderId: String,
        val price: Double
): DomainEvent

data class OrderRemoved(val orderId: String): DomainEvent
data class OrderCompleted(val orderId: String): DomainEvent

@SendToPerson("/orders/positions/status/preparing")
data class PositionStartPreparing(
        override val personId: String,
        val orderId: String,
        val positionId: String
): PersonDomainEvent
@SendToPerson("/orders/positions/status/done")
data class PositionDone(
        override val personId: String,
        val orderId: String,
        val positionId: String
): PersonDomainEvent
@SendToPerson("/orders/positions/status/delivered")
data class PositionDelivered(
        override val personId: String,
        val orderId: String,
        val positionId: String
): PersonDomainEvent
@SendToPerson("/orders/positions/cancel")
data class PositionCancelled(
        override val personId: String,
        val orderId: String,
        val positionId: String
): PersonDomainEvent