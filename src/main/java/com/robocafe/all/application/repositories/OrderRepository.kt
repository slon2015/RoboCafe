package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<Order, String> {
    fun findByIdAndClosedIsFalse(id: String): Order?
    fun findAllByPartyIdAndClosedIsFalse(partyId: String): Set<Order>
    fun findAllByPersonIdAndClosedIsFalse(personId: String): Set<Order>
}