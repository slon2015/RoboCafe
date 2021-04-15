package com.robocafe.core.domain

import org.springframework.data.domain.AbstractAggregateRoot
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

enum class PaymentStatus {
    SCHEDULED,
    OFFICIANT_MOVING,
    OFFICIANT_AWAITING_PAYMENT,
    FAILED,
    AWAITS_CONFIRMATION,
    CONFIRMED
}

@Entity
class Payment(
        @field:Id
        val id: String,
        val partyId: String,
        val personId: String?,
        val amount: Double,
        @field:Enumerated(EnumType.STRING)
        var status: PaymentStatus = PaymentStatus.SCHEDULED
): AbstractAggregateRoot<Payment>() {

    fun changeStatus(newStatus: PaymentStatus) {
        val oldStatus = status
        status = newStatus
        if (oldStatus != newStatus) {
            registerEvent(PaymentStatusChanged(id, newStatus))
        }
    }

    fun fail() {
        changeStatus(PaymentStatus.FAILED)
        registerEvent(PaymentFailed(id))
    }

    fun confirm() {
        changeStatus(PaymentStatus.CONFIRMED)
        registerEvent(PaymentConfirmed(id))
    }

    companion object {
        fun startPayment(id: String,
                         partyId: String,
                         personId: String?,
                         amount: Double): Payment {
            val payment = Payment(id, partyId, personId, amount)
            payment.registerEvent(PaymentCreated(id, partyId, personId, amount))
            return payment
        }
    }
}