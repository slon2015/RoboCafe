package com.robocafe.all.domain

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
        registerEvent(TableReleased(id))
    }

    fun cleanTable() {
        status = TableStatus.FREE
        registerEvent(TableCleaned(id))
    }

    init {
        status = TableStatus.FREE
        registerEvent(TableRegistered(id, tableNumber, maxPersons))
    }
}