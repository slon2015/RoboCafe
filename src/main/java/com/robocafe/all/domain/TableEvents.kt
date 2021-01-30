package com.robocafe.all.domain

import com.robocafe.all.domain.models.TableInfo
import com.robocafe.all.events.dispatching.SendToHall

data class TableRegistered(val tableId: String, val tableNum: Int, val maxPersons: Int): DomainEvent
data class TableOccupied(val tableId: String): DomainEvent
@SendToHall("/table/release")
data class TableReleased(val tableInfo: TableInfo): DomainEvent {
    override fun convertForHall(): Any {
        return mapOf("tableNum" to tableInfo.tableNumber)
    }
}
data class TableCleaned(val tableId: String): DomainEvent