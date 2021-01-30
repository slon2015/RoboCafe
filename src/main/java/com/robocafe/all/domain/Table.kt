package com.robocafe.all.domain

import com.robocafe.all.domain.models.TableInfo
import org.springframework.data.domain.AbstractAggregateRoot
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

enum class TableStatus {
    FREE, OCCUPIED, AWAITS_CLEANING
}

@Entity
@javax.persistence.Table(name = "cafe_table")
class Table(@field:Id val id: String, val tableNumber: Int, val maxPersons: Int) : AbstractAggregateRoot<Table?>() {
    @Enumerated(EnumType.STRING)
    var status: TableStatus

    fun occupyTable() {
        status = TableStatus.OCCUPIED
        registerEvent(TableOccupied(id))
    }

    fun freeTable() {
        status = TableStatus.AWAITS_CLEANING
        registerEvent(TableReleased(TableInfo(this)))
    }

    fun cleanTable() {
        status = TableStatus.FREE
        registerEvent(TableCleaned(id))
    }

    init {
        status = TableStatus.FREE
    }

    companion object {
        fun registerTable(id: String, tableNumber: Int, maxPersons: Int): Table {
            val table = Table(id, tableNumber, maxPersons)
            table.registerEvent(TableRegistered(id, tableNumber, maxPersons))
            return table
        }
    }
}