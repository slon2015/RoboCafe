package com.robocafe.all.application.handlers

import com.robocafe.all.application.services.OrderInfo
import com.robocafe.all.application.services.OrderPositionInfo
import com.robocafe.all.application.services.TableInfo
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workers/kitchen")
class KitchenController @Autowired constructor(private val sessionService: SessionService) {

    @PostMapping("/{orderId}/position/{positionId}/start")
    fun startPositionPrepared(orderId: String, positionId: String) {
        sessionService.startPositionPreparing(orderId, positionId)
    }

    @PostMapping("/{orderId}/position/{positionId}/finish")
    fun finishPositionPrepared(orderId: String, positionId: String) {
        sessionService.finishPositionPreparing(orderId, positionId)
    }

    @GetMapping("/view/orders/open")
    fun getOpenOrders(): Set<OrderInfo> {
        return sessionService.getOpenOrders()
    }

    @GetMapping("/view/positions/waiting/for/{orderId}")
    fun getWaitingPositionsForOrder(orderId: String): Set<OrderPositionInfo> {
        return sessionService.getPositionsForOrderThatWaitsForPreparing(orderId)
    }

    @GetMapping("/view/positions/preparing")
    fun getPreparingPositions(): Set<OrderPositionInfo> {
        return sessionService.getPositionsOnPreparingStage()
    }

    @GetMapping("/view/positions/delivering")
    fun getDeliveringPositions(): Set<OrderPositionInfo> {
        return sessionService.getPositionsOnDeliveringStage()
    }
}

@RestController
@RequestMapping("/workers/hall")
class HallController @Autowired constructor(private val sessionService: SessionService) {

    @PostMapping("/order/{orderId}/position/{positionId}/deliver")
    fun finishPositionDelivering(orderId: String, positionId: String) {
        sessionService.finishPositionDelivering(orderId, positionId)
    }

    @PostMapping("/table/{tableId}/clean")
    fun cleanTable(tableId: String) {
        sessionService.cleanTable(tableId)
    }

    @GetMapping("/view/tables")
    fun getAllTablesInfo(): Set<TableInfo> {
        return sessionService.getAllTablesInfo()
    }
}