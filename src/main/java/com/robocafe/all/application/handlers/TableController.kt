package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.RegisterTable
import com.robocafe.all.application.handlers.models.TableId
import com.robocafe.all.application.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/tables")
class TableController @Autowired constructor(private val tableService: TableService) {

    @PostMapping("/occupy")
    fun occupy(@RequestBody body: TableId): ResponseEntity<Void> {
        return try {
            tableService.occupyTable(body.id)
            ResponseEntity.ok().build()
        } catch (e: TableNotFound) {
            ResponseEntity.notFound().build()
        } catch (e: TableAlreadyOccupied) {
            ResponseEntity.badRequest().build()
        } catch (e: TableAwaitsCleaningOccupationFailed) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/release") //TODO remove this
    fun release(@RequestBody body: TableId): ResponseEntity<Void> {
        return try {
            tableService.releaseTable(body.id)
            ResponseEntity.ok().build()
        } catch (e: TableNotFound) {
            ResponseEntity.notFound().build()
        } catch (e: TableAlreadyReleased) {
            ResponseEntity.badRequest().build()
        } catch (e: TableAwaitsCleaningReleaseFailed) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/clean")
    fun clean(@RequestBody body: TableId): ResponseEntity<Void> {
        return try {
            tableService.cleanTable(body.id)
            ResponseEntity.ok().build()
        } catch (e: TableNotFound) {
            ResponseEntity.notFound().build()
        } catch (e: TableAlreadyClean) {
            ResponseEntity.badRequest().build()
        } catch (e: TableOccupiedCleanFailed) {
            ResponseEntity.badRequest().build()
        }
    }
}