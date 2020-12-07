package com.robocafe.all.domain

import org.springframework.data.domain.AbstractAggregateRoot
import java.util.*
import javax.persistence.*
import javax.persistence.Table

data class OrderPositionData(
        val menuPositionId: String,
        val count: Int
)

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

@Entity
class OrderPosition(
        @field:Id
        val id: String,
        val menuPositionId: String,
        @field:ManyToOne(optional = false)
        @field:JoinColumn(name = "order_id")
        val order: Order,
        @field:Enumerated(EnumType.STRING)
        var orderStatus: OrderStatus = OrderStatus.WAITING
)


@Entity
@Table(name = "menu_order")
class Order(
        @field:Id val id: String, val partyId: String, val personId: String?,
        positions: Set<OrderPositionData>,
        var price: Double,
): AbstractAggregateRoot<Order>() {

    var closed: Boolean = false
    @Enumerated(EnumType.STRING)
    var closeCause: CloseCause? = null

    @OneToMany(mappedBy = "order")
    val positions: Set<OrderPosition> = positions.flatMap {
        val mapped = mutableSetOf<OrderPosition>()
        for (i in 0 .. it.count) {
            mapped.add(OrderPosition(
                    UUID.randomUUID().toString(),
                    it.menuPositionId,
                    this
            ))
        }
        mapped
    }.toSet()

    fun changePrice(price: Double) {
        this.price = price
        registerEvent(OrderPriceChanged(id, price))
    }

    fun removeOrder() {
        closed = true
        closeCause = CloseCause.REMOVED
        registerEvent(OrderRemoved(id))
    }

    private fun findPositionInOrder(positionId: String) = positions.find { it.id == positionId }!!

    fun startPositionPreparing(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.PREPARING
        registerEvent(PositionStartPreparing(id, position.id))
    }

    fun preparePosition(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.DELIVERING
        registerEvent(PositionDone(id, position.id))
    }

    fun deliverPosition(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.COMPLETED
        registerEvent(PositionDelivered(id, position.id))
        if (positions.all { it.orderStatus == OrderStatus.COMPLETED }) {
            registerEvent(OrderCompleted(id))
        }
    }

    companion object {
        fun createOrder(id: String, partyId: String, personId: String?,
                        positions: Set<OrderPositionData>,
                        price: Double): Order {
            val order = Order(id, partyId, personId, positions, price)
            order.registerEvent(OrderCreated(id, partyId, personId,
                    order.positions.map { OrderPositionInfo(it) }.toSet()))
            return order
        }
    }
}