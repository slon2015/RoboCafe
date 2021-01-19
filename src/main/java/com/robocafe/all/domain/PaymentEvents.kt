package com.robocafe.all.domain

data class PaymentCreated(val paymentId: String,
                          val partyId: String,
                          val personId: String?,
                          val amount: Double): DomainEvent
data class PaymentStatusChanged(val paymentId: String, val newPaymentStatus: PaymentStatus): DomainEvent
data class PaymentFailed(val paymentId: String): DomainEvent
data class PaymentConfirmed(val paymentId: String): DomainEvent