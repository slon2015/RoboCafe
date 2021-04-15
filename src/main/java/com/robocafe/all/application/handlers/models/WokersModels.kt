package com.robocafe.all.application.handlers.models

import com.robocafe.core.domain.PaymentStatus

data class PaymentStatusChangeModel(
        val newStatus: PaymentStatus
)