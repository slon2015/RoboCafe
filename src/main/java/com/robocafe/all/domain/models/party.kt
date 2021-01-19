package com.robocafe.all.domain.models

import com.robocafe.all.domain.Person

data class PartyScopedTableInfo(
        val tableId: String,
        val tableNum: Int
)
data class PartyScopedPersonInfo(
        val id: String,
        val place: Int,
        val balance: Double
) {
    constructor(data: Person): this(data.id, data.placeOnTable, data.balance)
}