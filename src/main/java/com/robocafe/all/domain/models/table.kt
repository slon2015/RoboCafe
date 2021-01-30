package com.robocafe.all.domain.models

import com.robocafe.all.domain.Table
import com.robocafe.all.domain.TableStatus

data class TableInfo(
        val id: String, val tableNumber: Int, val maxPersons: Int, val status: TableStatus
) {
    constructor(data: Table): this(data.id, data.tableNumber, data.maxPersons, data.status)
}