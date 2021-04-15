package com.robocafe.core.domain

import com.robocafe.core.domain.models.TableInfo
import com.robocafe.core.events.dispatching.SendToHall

data class TableRegistered(val tableId: String, val tableNum: Int, val maxPersons: Int): DomainEvent
data class TableOccupied(val tableId: String): DomainEvent
@SendToHall("/table/release")
data class TableReleased(val tableInfo: TableInfo): DomainEvent {
    override fun convertForHall(): Any {
        return mapOf("tableNum" to tableInfo.tableNumber)
    }
}
data class TableCleaned(val tableId: String): DomainEvent