package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.TableRepository
import com.robocafe.all.domain.Table
import com.robocafe.all.domain.TableStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class TableInfo(
        val id: String, val tableNumber: Int, val maxPersons: Int, val status: TableStatus
) {
    constructor(data: Table): this(data.id, data.tableNumber, data.maxPersons, data.status)
}

@Service
class TableService @Autowired constructor(private val repository: TableRepository) {
    @Throws(TableWithSpecifiedNumAlreadyExists::class)
    fun registerTable(tableId: String, tableNumber: Int, maxPersons: Int) {
        if (repository.findByTableNumber(tableNumber) == null) {
            val newTable = Table.registerTable(tableId, tableNumber, maxPersons)
            repository.save(newTable)
        } else {
            throw TableWithSpecifiedNumAlreadyExists()
        }
    }

    @Throws(TableNotFound::class, TableAlreadyOccupied::class, TableAwaitsCleaningOccupationFailed::class)
    fun occupyTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }
        table.occupyTable()
        repository.save(table)
    }

    @Throws(TableNotFound::class, TableOccupiedCleanFailed::class, TableAlreadyClean::class)
    fun cleanTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }
        table.cleanTable()
        repository.save(table)
    }

    @Throws(TableNotFound::class, TableAwaitsCleaningReleaseFailed::class, TableAlreadyReleased::class)
    fun releaseTable(tableId: String) {
        val table = repository.findById(tableId).orElseThrow { TableNotFound() }
        table.freeTable()
        repository.save(table)
    }

    @Throws(TableNotFound::class)
    fun getTableInfo(tableId: String): TableInfo {
        return TableInfo(repository.findById(tableId).orElseThrow { TableNotFound() })
    }

    fun getAllTablesInfo(): Set<TableInfo> {
        return repository.findAll().map { TableInfo(it) }.toSet()
    }

    fun getTableByNumber(tableNum: Int): TableInfo {
        return TableInfo(repository.findByTableNumber(tableNum) ?: throw TableNotFound())
    }
}