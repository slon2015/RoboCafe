package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payment, String> {
    @Query("SELECT p FROM Payment p WHERE partyId = ?1 AND status = 'CONFIRMED'")
    fun findAllByPartyIdAndStatusEqualsConfirmed(partyId: String): Set<Payment>
    @Query("SELECT p FROM Payment p WHERE personId = ?1 AND status = 'CONFIRMED'")
    fun findAllByPersonIdAndStatusEqualsConfirmed(personId: String): Set<Payment>
}