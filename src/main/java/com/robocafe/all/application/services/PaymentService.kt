package com.robocafe.all.application.services

import com.robocafe.core.repositories.PaymentRepository
import com.robocafe.core.domain.Payment
import com.robocafe.core.domain.PaymentStatus
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
    constructor(data: Payment): this(
            data.id, data.partyId, data.personId, data.amount, data.status
    )
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

        private infix fun PaymentTarget.assertActivePaymentsDoesNotExistsIn(paymentRepository: PaymentRepository) {
            if (personId != null) {
                if (paymentRepository.existsActivePaymentsForPartyAndPerson(partyId, personId)) {
                    throw AlreadyHaveActivePayment()
                }
            } else {
                if (paymentRepository.existsByPartyIdAndStatusNotIsConfirmedAndStatusNotIsFailed(partyId)) {
                    throw AlreadyHaveActivePayment()
                }
            }
        }
    }
    fun createPayment(paymentId: String, target: PaymentTarget, amount: Double): PaymentInfo {
        target assertActivePaymentsDoesNotExistsIn paymentRepository
        val result = Payment.startPayment(
                paymentId,
                target.partyId,
                target.personId,
                amount
        )
        result.saveChanges()
        return PaymentInfo(result)
    }

    private fun Payment.saveChanges() {
        paymentRepository.save(this)
    }

    private fun findPayment(paymentId: String): Payment =
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

    fun confirmPayment(paymentId: String) {
        findPayment(paymentId) operate {
            assertStatusEquals(PaymentStatus.AWAITS_CONFIRMATION)
            confirm()
            saveChanges()
        }
    }

    fun getConfirmedPaymentsAmountForPerson(personId: String) =
            paymentRepository.findAllByPersonIdAndStatusEqualsConfirmed(personId)
                    .map<Payment, Double> { it.amount }.sum()
    fun getConfirmedPaymentsAmountForParty(partyId: String) =
            paymentRepository.findAllByPartyIdAndStatusEqualsConfirmed(partyId)
                    .map<Payment, Double> { it.amount }.sum()
    fun getPayment(paymentId: String) = PaymentInfo(findPayment(paymentId))
    fun getActivePayments() = paymentRepository.findAllActivePayments()
            .map<Payment, PaymentInfo> { PaymentInfo(it) }.toSet()
    fun getActivePaymentsForParty(partyId: String) =
            paymentRepository.findAllActivePaymentsByPartyId(partyId)
                    .map<Payment, PaymentInfo> { PaymentInfo(it) }.toSet()
}