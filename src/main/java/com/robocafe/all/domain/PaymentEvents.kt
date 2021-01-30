package com.robocafe.all.domain

import com.robocafe.all.events.dispatching.SendToHall


data class PaymentCreated(val paymentId: String,
                          val partyId: String,
                          val personId: String?,
                          val amount: Double): DomainEvent
@SendToHall("/payments/status/change")
data class PaymentStatusChanged(val paymentId: String, val newPaymentStatus: PaymentStatus): DomainEvent
@SendToHall("/payments/failed")
data class PaymentFailed(val paymentId: String): DomainEvent
@SendToHall("/payments/confirmed")
data class PaymentConfirmed(val paymentId: String): DomainEvent