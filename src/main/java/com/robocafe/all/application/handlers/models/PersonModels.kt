package com.robocafe.all.application.handlers.models

import com.robocafe.all.domain.OrderPositionData

data class MakeOrderModel(
        val positions: Set<OrderPositionData>
)