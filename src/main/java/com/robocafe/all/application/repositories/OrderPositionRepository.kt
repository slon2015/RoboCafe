package com.robocafe.all.application.repositories

import com.robocafe.all.domain.OrderPosition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderPositionRepository: JpaRepository<OrderPosition, String> {
    fun findAllByOrderIdAndOrderStatusEqualsWaiting(orderId: String): Set<OrderPosition>
    fun findAllByOrderStatusEqualsPreparing(): Set<OrderPosition>
    fun findAllByOrderStatusEqualsDelivering(): Set<OrderPosition>
}