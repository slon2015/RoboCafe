package com.robocafe.core.repositories

import com.robocafe.core.domain.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TableRepository : JpaRepository<Table, String> {
    fun findByTableNumber(tableNumber: Int): Table?
}