package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.PaymentStatus

data class PaymentStatusChangeModel(
        val newStatus: PaymentStatus
)