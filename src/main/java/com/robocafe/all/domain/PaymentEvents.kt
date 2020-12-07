package com.robocafe.all.domain

data class PaymentStatusChanged(val paymentId: String, val newPaymentStatus: PaymentStatus)
data class PaymentFailed(val paymentId: String)
data class PaymentConfirmed(val paymentId: String)