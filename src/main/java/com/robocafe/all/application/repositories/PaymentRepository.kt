package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payment, String> {
    fun findAllByPartyIdAndStatusEqualsConfirmed(partyId: String): Set<Payment>
    fun findAllByPersonIdAndStatusEqualsConfirmed(personId: String): Set<Payment>
}