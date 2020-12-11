package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.RegisterTable
import com.robocafe.all.application.handlers.models.TableId
import com.robocafe.all.application.handlers.models.TableRegistrationResponse
import com.robocafe.all.application.security.SecurityService
import com.robocafe.all.application.services.TableService
import com.robocafe.all.application.services.TableWithSpecifiedNumAlreadyExists
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/admin/tables")
@Transactional
class TableAdminController @Autowired constructor(
        private val sessionService: SessionService,
        private val securityService: SecurityService
) {

    @PostMapping
    fun register(@RequestBody body: RegisterTable): ResponseEntity<TableRegistrationResponse> {
        val id = UUID.randomUUID().toString()
        return try {
            sessionService.registerTable(id, body.tableNum, body.tableMaxPersons)
            val token = securityService.registerTable(id)
            ResponseEntity.ok(TableRegistrationResponse(id, token))
        } catch (e: TableWithSpecifiedNumAlreadyExists) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}