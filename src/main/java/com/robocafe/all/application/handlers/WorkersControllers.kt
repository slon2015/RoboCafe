package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.PaymentStatusChangeModel
import com.robocafe.all.application.security.SecurityService
import com.robocafe.all.application.services.*
import com.robocafe.all.domain.PaymentStatus
import com.robocafe.all.domain.models.TableInfo
import com.robocafe.all.session.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

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
@Transactional
class HallController @Autowired constructor(
        private val sessionService: SessionService,
        private val securityService: SecurityService
) {

    @PostMapping("/order/{orderId}/position/{positionId}/deliver")
    fun finishPositionDelivering(orderId: String, positionId: String) {
        sessionService.finishPositionDelivering(orderId, positionId)
    }

    @PostMapping("/table/{tableId}/clean")
    fun cleanTable(tableId: String) {
        sessionService.cleanTable(tableId)
    }

    @PostMapping("/payment/{paymentId}")
    fun changePaymentStatus(@PathVariable paymentId: String, @RequestBody body: PaymentStatusChangeModel) {
        when (body.newStatus) {
            PaymentStatus.OFFICIANT_MOVING -> sessionService.officiantMovedOutForPayment(paymentId)
            PaymentStatus.OFFICIANT_AWAITING_PAYMENT -> sessionService.officiantWaitsForPayment(paymentId)
            PaymentStatus.AWAITS_CONFIRMATION -> sessionService.paymentWaitsConfirmation(paymentId)
            else -> return
        }
    }

    @PostMapping("/payment/{paymentId}/confirm")
    fun confirmPayment(@PathVariable paymentId: String) {
        sessionService.confirmPayment(paymentId)
    }

    @PostMapping("/payment/{paymentId}/fail")
    fun failPayment(@PathVariable paymentId: String) {
        sessionService.failPayment(paymentId)
    }

    @GetMapping("/view/tables")
    fun getAllTablesInfo(): Set<TableInfo> {
        return sessionService.getAllTablesInfo()
    }

    @GetMapping("/view/payments")
    fun getActivePayments(): Set<PaymentInfo> = sessionService.getActivePayments()

}