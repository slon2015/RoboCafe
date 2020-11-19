package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.FullMenuInfo
import com.robocafe.all.menu.Category
import com.robocafe.all.menu.PositionInfo
import com.robocafe.all.menu.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/menu")
class MenuController @Autowired constructor(
        private val positionService: PositionService
) {

    @GetMapping
    fun fullMenu(): ResponseEntity<FullMenuInfo> {
        return ResponseEntity.ok(
                FullMenuInfo(positionService.getCategoryHierarchy(),
                    positionService.getAllPositions())
        )
    }

    @GetMapping("/for/{category}")
    fun menuForCategory(@PathVariable category: Category): ResponseEntity<Set<PositionInfo>> {
        return ResponseEntity.ok(positionService.getPositionsForCategory(category))
    }

    @GetMapping("/{id}")
    fun positionForId(@PathVariable id: String):ResponseEntity<PositionInfo> {
        return ResponseEntity.ok(positionService.getPositionInfo(id))
    }
}