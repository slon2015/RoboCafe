package com.robocafe.all.domain

data class TableRegistered(val tableId: String, val tableNum: Int, val maxPersons: Int)
data class TableOccupied(val tableId: String)
data class TableReleased(val tableId: String)
data class TableCleaned(val tableId: String)