package com.robocafe.all.domain

import org.springframework.data.domain.AbstractAggregateRoot
import javax.persistence.*
import javax.persistence.Table

enum class CloseCause {
    PAYED,
    REMOVED
}

enum class OrderStatus {
    WAITING,
    PREPARING,
    DELIVERING,
    COMPLETED
}

@Embeddable
class OrderPosition(
        val menuPositionId: String,
        val count: Int
)


@Entity
@Table(name = "menu_order")
class Order(
        @field:Id val id: String, val partyId: String, val personId: String?,
        @field:ElementCollection
        @field:CollectionTable(name = "order_positions")
        val positions: Set<OrderPosition>,
        var price: Double,
        var closed: Boolean = false,
        @field:Enumerated(EnumType.STRING) var closeCause: CloseCause? = null
): AbstractAggregateRoot<Order>() {

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.WAITING

    init {
        registerEvent(OrderCreated(id, partyId, personId,
                positions.map { OrderPositionInfo(it) }.toSet()))
    }

    fun changePrice(price: Double) {
        this.price = price
        registerEvent(OrderPriceChanged(id, price))
    }

    fun returnPaymentForOrder() {
        closed = false
        closeCause = null
        registerEvent(OrderPaymentRemoved(id, price))
    }

    fun payForOrder() {
        closed = true
        closeCause = CloseCause.PAYED
        registerEvent(OrderPayed(id, price))
    }

    fun removeOrder() {
        closed = true
        closeCause = CloseCause.REMOVED
        registerEvent(OrderRemoved(id))
    }

    fun startPreparingOrder() {
        val oldStatus = orderStatus
        orderStatus = OrderStatus.PREPARING
        registerEvent(OrderStatusChanged(id, orderStatus, oldStatus))
    }

    fun startDeliveringOrder() {
        val oldStatus = orderStatus
        orderStatus = OrderStatus.DELIVERING
        registerEvent(OrderStatusChanged(id, orderStatus, oldStatus))
    }

    fun completeOrder() {
        val oldStatus = orderStatus
        orderStatus = OrderStatus.COMPLETED
        registerEvent(OrderStatusChanged(id, orderStatus, oldStatus))
    }
}