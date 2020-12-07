package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.TableRepository
import com.robocafe.all.domain.Table
import com.robocafe.all.domain.TableStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class TableInfo(
        val id: String,
        val tableStatus: TableStatus,
        val tableNumber: Int,
        val tableMaxPersons: Int
) {
    constructor(data: Table): this(data.id, data.status, data.tableNumber, data.maxPersons)
}

@Service
class TableService @Autowired constructor(private val repository: TableRepository) {
    @Throws(TableWithSpecifiedNumAlreadyExists::class)
    fun registerTable(tableId: String, tableNumber: Int, maxPersons: Int) {
        if (repository.findByTableNumber(tableNumber) != null) {
            val newTable = Table(tableId, tableNumber, maxPersons)
            repository.save(newTable)
        } else {
            throw TableWithSpecifiedNumAlreadyExists()
        }
    }

    @Throws(TableNotFound::class, TableAlreadyOccupied::class, TableAwaitsCleaningOccupationFailed::class)
    fun occupyTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }!!
        when (table.status) {
            TableStatus.OCCUPIED -> throw TableAlreadyOccupied()
            TableStatus.AWAITS_CLEANING -> throw TableAwaitsCleaningOccupationFailed()
            TableStatus.FREE -> {
                table.occupyTable()
                repository.save(table)
            }
        }
    }

    @Throws(TableNotFound::class, TableOccupiedCleanFailed::class, TableAlreadyClean::class)
    fun cleanTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }
        when (table.status) {
            TableStatus.OCCUPIED -> throw TableOccupiedCleanFailed()
            TableStatus.AWAITS_CLEANING -> {
                table.cleanTable()
                repository.save(table)
            }
            TableStatus.FREE -> throw TableAlreadyClean()
        }
    }

    @Throws(TableNotFound::class, TableAwaitsCleaningReleaseFailed::class, TableAlreadyReleased::class)
    fun releaseTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }
        when (table.status) {
            TableStatus.OCCUPIED -> {
                table.freeTable()
                repository.save(table)
            }
            TableStatus.AWAITS_CLEANING -> throw TableAwaitsCleaningReleaseFailed()
            TableStatus.FREE -> throw TableAlreadyReleased()
        }
    }

    @Throws(TableNotFound::class)
    fun getTableInfo(tableId: String): TableInfo {
        return TableInfo(repository.findById(tableId).orElseThrow { TableNotFound() })
    }
}