package com.robocafe.all.session

import com.robocafe.all.afiche.AficheService
import com.robocafe.all.application.handlers.models.HallStateInitInfo
import com.robocafe.all.application.services.*
import com.robocafe.all.domain.*
import com.robocafe.all.domain.models.ChatInfo
import com.robocafe.all.domain.models.ChatMemberInfo
import com.robocafe.all.domain.models.DetalizedChatMemberInfo
import com.robocafe.all.domain.models.PartyScopedPersonInfo
import com.robocafe.all.hallscheme.HallStateService
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
        private val aficheService: AficheService,
        private val orderService: OrderService,
        private val paymentService: PaymentService,
        private val sessionRepository: SessionRepository,
        private val hallStateService: HallStateService
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

        private fun PartyInfo.assertPlaceNotOccupied(place: Int) {
            if (members.any { it.place == place}) {
                throw PlaceAlreadyOccupied()
            }
        }

        private fun PartyInfo.assertPersonInParty(personId: String) {
            if (members.all { it.id != personId }) {
                throw PersonNotInParty()
            }
        }

        private fun PartyInfo.assertUnpayedBalanceGreaterOrEquals(target: PaymentTarget,
                                                                  amount: Double,
                                                                  sessionService: SessionService) {
            val unpayedBalance = if (target.personId != null)
                sessionService.getUnpayedBalanceForPerson(target.personId)
            else
                sessionService.getUnpayedBalanceForParty(target.partyId)
            if (unpayedBalance < amount) {
                throw InvalidPaymentAmount()
            }
        }

        private fun PartyInfo.assertUnpayedBalanceEquals(amount: Double,
                                                         sessionService: SessionService) {
            if (sessionService.getUnpayedBalanceForParty(id) != amount) {
                throw InvalidPaymentAmount()
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

        private fun PartyScopedPersonInfo.assertBalancePayed(sessionService: SessionService) {
            if (sessionService.getUnpayedBalanceForPerson(id) > 0) {
                throw BalanceForPersonNotPayed()
            }
        }

        private infix fun PartyScopedPersonInfo.assertPersonHaveNoOpenOrdersIn(orderService: OrderService) {
            val orders = orderService.getOpenOrdersForPerson(id)
            if (orders.isNotEmpty()) {
                throw PersonHasOpenOrders(orders)
            }
        }

        private fun Collection<DetalizedChatMemberInfo>.assertMembersMoreThan1() {
            if (this.size <= 1) {
                throw OnlyOneMember()
            }
        }

        private fun DetalizedChatMemberInfo.assertMemberIsValid(personService: PersonService, partyService: PartyService) {
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

        private fun Set<OrderPositionData>.assertOrderNotEmpty() {
            if (isEmpty()) {
                throw OrderContentIsEmpty()
            }
            forEach {
                if (it.count <= 0) {
                    throw InvalidOrderPosition()
                }
            }
        }

        private fun assertAmountGreaterThanZero(amount: Double) {
            if (amount <= 0.0) {
                throw PaymentAmountLowerOrEqualsZero()
            }
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

    data class TableInitInfo(
            val tableId: String,
            val tableStatus: TableStatus,
            val tableNum: Int,
            val partyId: String?,
            val persons: List<String>?

    )

    fun getInitDataForTable(tableId: String): TableInitInfo {
        val table = tableService.getTableInfo(tableId)
        val party = if(table.status == TableStatus.OCCUPIED) partyService.getActivePartyForTable(tableId) else null
        return TableInitInfo(
                table.id,
                table.status,
                table.tableNumber,
                party?.id,
                party?.members?.map { it.id }
        )
    }

    fun getTableInfo(tableId: String) = tableService.getTableInfo(tableId)
    fun getAllTablesInfo() = tableService.getAllTablesInfo()

    fun getParty(partyId: String) = partyService.getParty(partyId)
    fun getPartyForPerson(personId: String) = partyService.getPartyForPerson(personId)

    fun notEndedPartyExists(partyId: String) = partyService.notEndedPartyExists(partyId)

    fun joinPerson(partyId: String, personId: String, place: Int) {
        partyService.getParty(partyId) operate {
            assertPartyNotFull()
            assertPlaceNotOccupied(place)
            partyService.joinPerson(partyId, personId, place, tableService.getTableInfo(tableId))
        }
    }

    fun removePersonFromParty(partyId: String, personId: String) {
        partyService.getParty(partyId) operate {
            assertPersonInParty(personId)
            personService.getPerson(personId) operate {
                assertPersonHaveNoOpenOrdersIn(orderService)
                assertBalancePayed(this@SessionService)
                val tableNum = tableService.getTableInfo(tableId).tableNumber
                partyService.removePersonFromParty(tableNum, partyId, personId)
            }
        }
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        partyService.getParty(partyId) operate {
            assertPersonInParty(memberId)
            partyService.changeMemberBalance(partyId, memberId, amount)
        }
    }

    fun startChat(
            chatId: String,
            chatName: String,
            memberIds: Set<DetalizedChatMemberInfo>
    ): ChatInfo {
        memberIds.assertMembersMoreThan1()
        memberIds.forEach { it.assertMemberIsValid(personService, partyService) }
        return chatService.startChat(chatId, chatName, memberIds)
    }

    fun findChat(chatId: String) = chatService.findChat(chatId)

    fun addMemberToChat(chatId: String, member: DetalizedChatMemberInfo) {
        chatService.findChat(chatId) operate {
            assertMemberNotInChat(member.partyId, member.personId)
            member.assertMemberIsValid(personService, partyService)
            chatService.addMemberToChat(chatId, member)
        }
    }

    fun removeMemberFromChat(chatId: String, memberId: DetalizedChatMemberInfo) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId.partyId, memberId.personId)
            chatService.removeMemberFromChat(chatId, memberId)
        }
    }

    fun sendMessage(messageId: String, chatId: String, memberId: DetalizedChatMemberInfo, text: String) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId.partyId, memberId.personId)
            chatService.sendMessage(messageId, chatId, memberId, text)
        }
    }

    fun createOrder(orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>): OrderInfo {
        positions.assertOrderNotEmpty()
        orderAuthorData checkPartyIn(partyService) checkMemberIn(personService)
        val price = positions calculatePriceWith positionService
        val orderId = UUID.randomUUID().toString()
        return orderService.createOrder(orderId, orderAuthorData, positions, price)
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
            party = partyService.startParty(this, partyId, membersCount)
        }
        return party
    }

    fun endSessionForTable(tableId: String) {
        partyService.getActivePartyForTable(tableId) operate {
            tableService.getTableInfo(tableId) operate {
                assertStatusEquals(TableStatus.OCCUPIED)
            }
            endSession(this)
        }
    }

    fun endSession(party: PartyInfo) {
        party operate {
            assertMembersHaveNoOpenOrdersIn(orderService)
            assertBalancePayed(this@SessionService)
            val tableNum = tableService.getTableInfo(tableId).tableNumber
            partyService.endParty(tableNum, id)
            tableService.releaseTable(tableId)
        }
    }

    fun createPayment(target: PaymentTarget, amount: Double): PaymentInfo {
        lateinit var payment: PaymentInfo
        val paymentId = UUID.randomUUID().toString()
        assertAmountGreaterThanZero(amount)
        partyService.getParty(target.partyId) operate {
            if (target.personId != null) {
                assertPersonInParty(target.personId)
                assertUnpayedBalanceGreaterOrEquals(target, amount, this@SessionService)
            }
            else {
                assertUnpayedBalanceEquals(amount, this@SessionService)
            }
            payment = paymentService.createPayment(paymentId, target, amount)
        }
        return payment
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

    fun confirmPayment(paymentId: String) {
        paymentService.getPayment(paymentId) operate {
            paymentService.confirmPayment(paymentId)
            if (personId != null) {
                endSession(partyService.getParty(partyId))
            }
        }
    }

    fun getHallState(): HallStateInitInfo {
        val staticState = hallStateService.getHallState()
        val tableMappings = tableService.getAllTablesInfo().map { it.tableNumber to it.id }.toMap()
        return HallStateInitInfo(
                staticState,
                staticState.tableRects.keys
                        .map { it to tableMappings[it] }
                        .map { it.first to it.second?.let { tableId ->
                                partyService.getOptionalPartyForTable(tableId)?.members
                                        ?.map { member -> member.place }?.toSet()
                            }
                        }.toMap()
        )
    }

    fun getActivePayments() = paymentService.getActivePayments()

    fun getOpenOrders() = orderService.getOpenOrders()
    fun getOpenOrdersForPerson(personId: String) = orderService.getOpenOrdersForPerson(personId)
    fun getPositionsForOrderThatWaitsForPreparing(orderId: String) =
            orderService.getPositionsForOrderThatWaitsForPreparing(orderId)
    fun getPositionsOnPreparingStage() =
            orderService.getPositionsOnPreparingStage()
    fun getPositionsOnDeliveringStage() =
            orderService.getPositionsOnDeliveringStage()
    fun getAfichesList() = aficheService.getAfichePreviews()
    fun getAficheContent(aficheId: String) = aficheService.getAficheContent(aficheId)
    fun getChatsForPerson(personId: String) =
            chatService.getChatsFor(ChatMemberInfo(getPartyForPerson(personId).id, personId))
    fun getPlaceForPerson(personId: String) = personService.getPerson(personId).place
    fun getTableByNumber(tableNumber: Int) = tableService.getTableByNumber(tableNumber)
    fun getActivePartyForTable(tableId: String) = partyService.getActivePartyForTable(tableId)
    fun getPersonFromPartyByPlace(partyId: String, place: Int) = personService.getPersonFromPartyByPlace(partyId, place)
    fun getPerson(personId: String) = personService.getPerson(personId)
    fun getActivePaymentsForParty(partyId: String) = paymentService.getActivePaymentsForParty(partyId)
}