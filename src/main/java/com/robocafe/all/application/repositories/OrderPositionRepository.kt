package com.robocafe.all.application.repositories

import com.robocafe.all.domain.OrderPosition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderPositionRepository: JpaRepository<OrderPosition, String> {
    @Query("SELECT op FROM OrderPosition op WHERE orderStatus = 'WAITING' AND orderId = ?1")
    fun findAllByOrderIdAndOrderStatusEqualsWaiting(orderId: String): Set<OrderPosition>
    @Query("SELECT op FROM OrderPosition op WHERE orderStatus = 'PREPARING'")
    fun findAllByOrderStatusEqualsPreparing(): Set<OrderPosition>
    @Query("SELECT op FROM OrderPosition op WHERE orderStatus = 'DELIVERING'")
    fun findAllByOrderStatusEqualsDelivering(): Set<OrderPosition>
}