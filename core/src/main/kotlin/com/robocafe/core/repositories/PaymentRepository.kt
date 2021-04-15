package com.robocafe.core.repositories

import com.robocafe.core.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payment, String> {
    @Query("SELECT p FROM Payment p WHERE partyId = ?1 AND status = 'CONFIRMED'")
    fun findAllByPartyIdAndStatusEqualsConfirmed(partyId: String): Set<Payment>
    @Query("SELECT p FROM Payment p WHERE personId = ?1 AND status = 'CONFIRMED'")
    fun findAllByPersonIdAndStatusEqualsConfirmed(personId: String): Set<Payment>
    @Query("SELECT CASE WHEN COUNT(p)> 0 THEN TRUE ELSE FALSE END " +
            "FROM Payment p WHERE partyId = ?1 AND personId = ?2 " +
            "AND status != 'CONFIRMED' AND status != 'FAILED'")
    fun existsActivePaymentsForPartyAndPerson(partyId: String, personId: String): Boolean
    @Query("SELECT CASE WHEN COUNT(p)> 0 THEN TRUE ELSE FALSE END " +
            "FROM Payment p WHERE partyId = ?1 " +
            "AND status != 'CONFIRMED' AND status != 'FAILED'")
    fun existsByPartyIdAndStatusNotIsConfirmedAndStatusNotIsFailed(partyId: String): Boolean
    @Query("SELECT p FROM Payment p WHERE status != 'CONFIRMED' AND status != 'FAILED'")
    fun findAllActivePayments(): Set<Payment>
    @Query("SELECT p FROM Payment p WHERE status != 'CONFIRMED' AND status != 'FAILED' AND partyId = ?1")
    fun findAllActivePaymentsByPartyId(partyId: String): Set<Payment>
}