package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.RegisterTable
import com.robocafe.all.application.services.TableService
import com.robocafe.all.application.services.TableWithSpecifiedNumAlreadyExists
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/admin/tables")
class TableAdminController @Autowired constructor(private val sessionService: SessionService) {

    @PostMapping
    fun register(@RequestBody body: RegisterTable): ResponseEntity<String> {
        val id = UUID.randomUUID().toString()
        return try {
            sessionService.registerTable(id, body.tableNum, body.tableMaxPersons)
            ResponseEntity.ok(id)
        } catch (e: TableWithSpecifiedNumAlreadyExists) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}