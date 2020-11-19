package com.robocafe.all.domain

data class OrderPositionInfo(val menuPositionId: String, val count: Int) {
    constructor(data: OrderPosition): this(data.menuPositionId, data.count)
}

data class OrderCreated(
        val id: String,
        val partyId: String,
        val personId: String?,
        val positions: Set<OrderPositionInfo>)

data class OrderPriceChanged(
        val orderId: String,
        val price: Double
)

data class OrderPayed(val orderId: String, val price: Double)
data class OrderRemoved(val orderId: String)
data class OrderPaymentRemoved(val orderId: String, val price: Double)
data class OrderStatusChanged(val orderId: String,
                              val newOrderStatus: OrderStatus,
                              val oldOrderStatus: OrderStatus)