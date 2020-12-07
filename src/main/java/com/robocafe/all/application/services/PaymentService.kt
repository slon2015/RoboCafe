package com.robocafe.all.application.services

import com.robocafe.all.application.repositories.PaymentRepository
import com.robocafe.all.domain.Payment
import com.robocafe.all.domain.PaymentStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

data class PaymentTarget(
        val partyId: String,
        val personId: String?
)

data class PaymentInfo(
        val id: String,
        val partyId: String,
        val personId: String?,
        val amount: Double,
        var status: PaymentStatus = PaymentStatus.SCHEDULED
) {
    constructor(data: Payment): this(data.id, data.partyId, data.personId, data.amount, data.status)
}

@Service
class PaymentService @Autowired constructor(
        private val paymentRepository: PaymentRepository
) {
    private companion object Dsl {
        private infix fun Payment.operate(operation: Payment.() -> Unit) {
            operation(this)
        }

        private fun Payment.assertStatusEquals(status: PaymentStatus) {
            if (this.status != status) {
                throw InvalidPaymentStatus()
            }
        }

        private fun Payment.assertStatusNotEquals(vararg statuses: PaymentStatus) {
            if (statuses.contains(status)) {
                throw InvalidPaymentStatus()
            }
        }
    }
    fun createPayment(paymentId: String, target: PaymentTarget, amount: Double) {
        Payment(
                paymentId,
                target.partyId,
                target.personId,
                amount
        ).saveChanges()
    }

    private fun Payment.saveChanges() {
        paymentRepository.save(this)
    }

    private fun findPayment(paymentId: String) =
            paymentRepository.findById(paymentId).orElseThrow { PaymentNotFound() }

    fun officiantMovedOutForPayment(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusEquals(PaymentStatus.SCHEDULED)
            changeStatus(PaymentStatus.OFFICIANT_MOVING)
            saveChanges()
        }
    }

    fun officiantWaitsForPayment(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusEquals(PaymentStatus.OFFICIANT_MOVING)
            changeStatus(PaymentStatus.OFFICIANT_AWAITING_PAYMENT)
            saveChanges()
        }
    }

    fun failPayment(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusNotEquals(PaymentStatus.FAILED, PaymentStatus.CONFIRMED)
            fail()
            saveChanges()
        }
    }

    fun paymentWaitsConfirmation(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusEquals(PaymentStatus.OFFICIANT_AWAITING_PAYMENT)
            changeStatus(PaymentStatus.AWAITS_CONFIRMATION)
            saveChanges()
        }
    }

    fun paymentConfirmed(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusEquals(PaymentStatus.AWAITS_CONFIRMATION)
            confirm()
            saveChanges()
        }
    }

    fun getConfirmedPaymentsAmountForPerson(personId: String) =
            paymentRepository.findAllByPersonIdAndStatusEqualsConfirmed(personId)
                    .map { it.amount }.sum()
    fun getConfirmedPaymentsAmountForParty(partyId: String) =
            paymentRepository.findAllByPartyIdAndStatusEqualsConfirmed(partyId)
                    .map { it.amount }.sum()
}