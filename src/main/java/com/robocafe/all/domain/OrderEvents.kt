package com.robocafe.all.domain

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

data class PositionStartPreparing(val orderId: String, val positionId: String): DomainEvent
data class PositionDone(val orderId: String, val positionId: String): DomainEvent
data class PositionDelivered(val orderId: String, val positionId: String): DomainEvent