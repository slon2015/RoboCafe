package com.robocafe.all.session

import com.robocafe.all.application.services.*
import com.robocafe.all.domain.*
import com.robocafe.all.menu.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class SessionService @Autowired constructor(
        private val tableService: TableService,
        private val personService: PersonService,
        private val partyService: PartyService,
        private val chatService: ChatService,
        private val positionService: PositionService,
        private val orderService: OrderService,
        private val paymentService: PaymentService,
        private val sessionRepository: SessionRepository
) {

    internal companion object Dsl {
        private infix fun <T> T.operate(operation: T.() -> Unit) {
            operation(this)
        }

        private fun TableInfo.assertStatusEquals(expected: TableStatus) {
            if (status != expected) {
                throw IncorrectTableStatus()
            }
        }

        private fun PartyInfo.assertPartyNotFull() {
            if (members.size == maxMembers) {
                throw PartyAlreadyFull()
            }
        }

        private fun PartyInfo.assertPersonInParty(personId: String) {
            if (members.all { it.id != personId }) {
                throw PersonNotInParty()
            }
        }

        private fun PartyInfo.assertUnpayedBalanceGreaterOrEquals(target: PaymentTarget,
                                                                  amount: Double,
                                                                  orderService: OrderService,
                                                                  paymentService: PaymentService) {
            val balance = if (target.personId == null) orderService.getBalanceForParty(id)
                else orderService.getBalanceForPerson(target.personId)
            val payedBalance = if (target.personId == null) paymentService.getConfirmedPaymentsAmountForParty(id)
                else paymentService.getConfirmedPaymentsAmountForPerson(target.personId)
            if (balance - payedBalance < amount) {
                throw PaymentTargetBalanceLowerThanAmount()
            }
        }

        private fun PartyInfo.assertMembersHaveNoOpenOrdersIn(orderService: OrderService) {
            val orders = orderService.getOpenOrdersForParty(id)
            if (orders.isNotEmpty()) {
                throw PartyHasOpenOrders(orders)
            }
        }

        private infix fun PartyInfo.assertBalancePayed(sessionService: SessionService) {
            if (sessionService.getUnpayedBalanceForParty(id) > 0) {
                throw BalanceForPartyNotPayed()
            }
        }

        private fun PersonInfo.assertBalancePayed(sessionService: SessionService) {
            if (sessionService.getUnpayedBalanceForPerson(id) > 0) {
                throw BalanceForPersonNotPayed()
            }
        }

        private infix fun PersonInfo.assertPersonHaveNoOpenOrdersIn(orderService: OrderService) {
            val orders = orderService.getOpenOrdersForPerson(id)
            if (orders.isNotEmpty()) {
                throw PersonHasOpenOrders(orders)
            }
        }

        private fun ChatMemberId.assertMemberIsValid(personService: PersonService, partyService: PartyService) {
            if (!partyService.notEndedPartyExists(partyId)) {
                throw MembersPartyEnded()
            }
            if (!personService.personWithActivePartyExists(personId)) {
                throw InvalidPersonId()
            }
        }

        private fun ChatInfo.assertMemberNotInChat(partyId: String, personId: String) {
            if (members.any { it.partyId == partyId && it.personId == personId }) {
                throw MemberAlreadyInChat()
            }
        }

        private fun ChatInfo.assertMemberInChat(partyId: String, personId: String) {
            if (members.all {it.partyId != partyId || it.personId != personId}) {
                throw MemberNotInChat()
            }
        }

        private infix fun OrderAuthorData.checkPartyIn(partyService: PartyService): OrderAuthorData {
            if (!partyService.notEndedPartyExists(partyId)) {
                throw InvalidParty()
            }
            return this
        }

        private infix fun OrderAuthorData.checkMemberIn(personService: PersonService): OrderAuthorData {
            if (!personService.personWithActivePartyExists(memberId)) {
                throw InvalidPerson()
            }
            return this
        }

        private infix fun Set<OrderPositionData>.calculatePriceWith(positionService: PositionService): Double {
            return map { it to positionService.getPositionInfo(it.menuPositionId) }
                    .map { it.second.price * it.first.count }.sum()
        }
    }

    fun registerTable(tableId: String, tableNumber: Int, maxPersons: Int) {
        tableService.registerTable(tableId, tableNumber, maxPersons)
    }

    fun cleanTable(tableId: String) {
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.AWAITS_CLEANING)
            tableService.cleanTable(tableId)
        }
    }

    fun getTableInfo(tableId: String) = tableService.getTableInfo(tableId)
    fun getAllTablesInfo() = tableService.getAllTablesInfo()

    fun getParty(partyId: String) = partyService.getParty(partyId)

    fun notEndedPartyExists(partyId: String) = partyService.notEndedPartyExists(partyId)

    fun joinPerson(partyId: String, personId: String) {
        partyService.getParty(partyId) operate {
            assertPartyNotFull()
            partyService.joinPerson(partyId, personId)
        }
    }

    fun removePersonFromParty(partyId: String, personId: String) {
        partyService.getParty(partyId) operate {
            assertPersonInParty(personId)
            personService.getPerson(personId) operate {
                assertPersonHaveNoOpenOrdersIn(orderService)
                assertBalancePayed(this@SessionService)
                partyService.removePersonFromParty(partyId, personId)
            }
        }
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        partyService.getParty(partyId) operate {
            assertPersonInParty(memberId)
            partyService.changeMemberBalance(partyId, memberId, amount)
        }
    }

    fun startChat(chatId: String, memberIds: Set<ChatMemberId>): ChatInfo {
        memberIds.forEach { it.assertMemberIsValid(personService, partyService) }
        return chatService.startChat(chatId, memberIds)
    }

    fun findChat(chatId: String) = chatService.findChat(chatId)

    fun addMemberToChat(uniqueMemberId: String, chatId: String, member: ChatMemberId) {
        chatService.findChat(chatId) operate {
            assertMemberNotInChat(member.partyId, member.personId)
            member.assertMemberIsValid(personService, partyService)
            chatService.addMemberToChat(chatId, member)
        }
    }

    fun removeMemberFromChat(chatId: String, memberId: ChatMemberId) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId.partyId, memberId.personId)
            chatService.removeMemberFromChat(chatId, memberId)
        }
    }

    fun sendMessage(messageId: String, chatId: String, memberId: ChatMemberId, text: String) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId.partyId, memberId.personId)
            chatService.sendMessage(messageId, chatId, memberId, text)
        }
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>) {
        orderAuthorData checkPartyIn(partyService) checkMemberIn(personService)
        val price = positions calculatePriceWith positionService
        orderService.createOrder(id, orderAuthorData, positions, price)
    }

    fun getUnpayedBalanceForParty(partyId: String) =
            orderService.getBalanceForParty(partyId) - paymentService.getConfirmedPaymentsAmountForParty(partyId)

    fun getUnpayedBalanceForPerson(personId: String) =
            orderService.getBalanceForPerson(personId) - paymentService.getConfirmedPaymentsAmountForPerson(personId)

    fun changeOrderPrice(orderId: String, newPrice: Double) {
        orderService.changeOrderPrice(orderId, newPrice)
    }

    fun removeOrder(orderId: String) {
        orderService.removeOrder(orderId)
    }

    fun startPositionPreparing(orderId: String, positionId: String) {
        orderService.startPositionPreparing(orderId, positionId)
    }

    fun finishPositionPreparing(orderId: String, positionId: String) {
        orderService.finishPositionPreparing(orderId, positionId)
    }

    fun finishPositionDelivering(orderId: String, positionId: String) {
        orderService.finishPositionDelivering(orderId, positionId)
    }

    fun startSession(tableId: String, partyId: String, membersCount: Int): PartyInfo {
        lateinit var party: PartyInfo
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.FREE)
            tableService.occupyTable(tableId)
            party = partyService.startParty(tableId, partyId, maxPersons, membersCount)
        }
        return party
    }

    fun endSessionForTable(tableId: String) {
        partyService.getActivePartyForTable(tableId) operate {
            tableService.getTableInfo(tableId) operate {
                assertStatusEquals(TableStatus.OCCUPIED)
            }
            assertMembersHaveNoOpenOrdersIn(orderService)
            assertBalancePayed(this@SessionService)
            partyService.endParty(id)
            tableService.releaseTable(tableId)
        }
    }

    fun createPayment(paymentId: String, target: PaymentTarget, amount: Double) {
        partyService.getParty(target.partyId) operate {
            if (target.personId != null) {
                assertPersonInParty(target.personId)
            }
            assertUnpayedBalanceGreaterOrEquals(target, amount, orderService, paymentService)
            paymentService.createPayment(paymentId, target, amount)
        }
    }

    fun officiantMovedOutForPayment(paymentId: String) {
        paymentService.officiantMovedOutForPayment(paymentId)
    }

    fun officiantWaitsForPayment(paymentId: String) {
        paymentService.officiantWaitsForPayment(paymentId)
    }

    fun failPayment(paymentId: String) {
        paymentService.failPayment(paymentId)
    }

    fun paymentWaitsConfirmation(paymentId: String) {
        paymentService.paymentWaitsConfirmation(paymentId)
    }

    fun paymentConfirmed(paymentId: String) {
        paymentService.paymentConfirmed(paymentId)
    }

    fun getOpenOrders() = orderService.getOpenOrders()
    fun getPositionsForOrderThatWaitsForPreparing(orderId: String) =
            orderService.getPositionsForOrderThatWaitsForPreparing(orderId)
    fun getPositionsOnPreparingStage() =
            orderService.getPositionsOnPreparingStage()
    fun getPositionsOnDeliveringStage() =
            orderService.getPositionsOnDeliveringStage()
}