package com.robocafe.all.application.services

class PaymentNotFound: EntityNotFound()
class InvalidPaymentStatus: DomainException()
class AlreadyHaveActivePayment: DomainException()