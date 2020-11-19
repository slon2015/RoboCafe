package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TableRepository : JpaRepository<Table, String> {
    fun findByTableNumber(tableNumber: Int): Table?
}