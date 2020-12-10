package com.robocafe.all.domain

data class OrderPositionPoint(val menuPositionId: String,
                              val positionId: String) {
    constructor(data: OrderPosition): this(data.menuPositionId, data.id)
}

data class OrderCreated(
        val id: String,
        val partyId: String,
        val personId: String,
        val positions: Set<OrderPositionPoint>)

data class OrderPriceChanged(
        val orderId: String,
        val price: Double
)

data class OrderRemoved(val orderId: String)
data class OrderCompleted(val orderId: String)

data class PositionStartPreparing(val orderId: String, val positionId: String)
data class PositionDone(val orderId: String, val positionId: String)
data class PositionDelivered(val orderId: String, val positionId: String)