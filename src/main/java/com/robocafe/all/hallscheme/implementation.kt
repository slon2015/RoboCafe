package com.robocafe.all.hallscheme

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Table_Rect")
class TableRect(@field:Id val number: Int, val ltX: Double, val ltY: Double, val rbX: Double, val rbY: Double)

@Repository
interface TableRectRepository: JpaRepository<TableRect, Int>

class Rect(tableRect: TableRect) {
    val ltX = tableRect.ltX
    val ltY = tableRect.ltY
    val rbX = tableRect.rbX
    val rbY = tableRect.rbY
}

data class HallState(
        val hallSchemeImage: String,
        val schemeImageWidth: Int,
        val tableRects: Map<Int, Rect>
)

@Service
class HallStateService @Autowired constructor(
        @Value("\${hall.image}")
        private val hallImagePath: String,
        @Value("\${hall.imageBaseWidth}")
        private val hallImageBaseWidth: Int,
        private val tableRectRepository: TableRectRepository
) {
    fun getHallState(): HallState {
        val rects = tableRectRepository.findAll()
        return HallState(
                hallImagePath,
                hallImageBaseWidth,
                rects.map { it.number to Rect(it) }.toMap()
        )
    }
}