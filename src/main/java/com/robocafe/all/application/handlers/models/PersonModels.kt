package com.robocafe.all.application.handlers.models

import com.robocafe.core.domain.OrderPositionData

data class MakeOrderModel(
        val positions: Set<OrderPositionData>
)