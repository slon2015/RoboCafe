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
    REMOVED,
    COMPLETED
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
        @field:Id val id: String, val partyId: String, val personId: String,
        positions: Set<OrderPositionData>,
        var price: Double,
): AbstractAggregateRoot<Order>() {

    var closed: Boolean = false
    @Enumerated(EnumType.STRING)
    var closeCause: CloseCause? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val positions: MutableSet<OrderPosition> = positions.flatMap {
        val mapped = mutableSetOf<OrderPosition>()
        for (i in 1 .. it.count) {
            mapped.add(OrderPosition(
                    UUID.randomUUID().toString(),
                    it.menuPositionId,
                    this
            ))
        }
        mapped
    }.toMutableSet()

    fun changePrice(price: Double) {
        this.price = price
        registerEvent(OrderPriceChanged(id, price))
    }

    fun removeOrder() {
        closed = true
        closeCause = CloseCause.REMOVED
        registerEvent(OrderRemoved(id))
    }

    fun cancelPosition(orderPosition: OrderPosition, personId: String) {
        positions.remove(orderPosition)
        registerEvent(PositionCancelled(personId, id, orderPosition.id))
    }

    private fun findPositionInOrder(positionId: String) = positions.find { it.id == positionId }!!

    fun startPositionPreparing(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.PREPARING
        registerEvent(PositionStartPreparing(personId, id, position.id))
    }

    fun preparePosition(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.DELIVERING
        registerEvent(PositionDone(personId, id, position.id))
    }

    fun deliverPosition(positionId: String) {
        val position = findPositionInOrder(positionId)
        position.orderStatus = OrderStatus.COMPLETED
        registerEvent(PositionDelivered(personId, id, position.id))
        if (positions.all { it.orderStatus == OrderStatus.COMPLETED }) {
            closed = true
            closeCause = CloseCause.COMPLETED
            registerEvent(OrderCompleted(id))
        }
    }

    companion object {
        fun createOrder(id: String, partyId: String, personId: String,
                        positions: Set<OrderPositionData>,
                        price: Double): Order {
            val order = Order(id, partyId, personId, positions, price)
            order.registerEvent(OrderCreated(id, partyId, personId,
                    order.positions.map { OrderPositionPoint(it) }.toSet()))
            return order
        }
    }
}