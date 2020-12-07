package com.robocafe.all.session

import com.robocafe.all.application.services.*
import com.robocafe.all.domain.*
import com.robocafe.all.menu.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
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
        private val sessionRepository: SessionRepository
) {

    private companion object Dsl {
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

        private fun instantiateMember(uniqueMemberId: String, domainId: MemberId): ChatMemberInfo {
            return ChatMemberInfo(
                    uniqueMemberId,
                    if (domainId is PartyMemberId) domainId.id else null,
                    if (domainId is PersonMemberId) domainId.id else null,
                    when(domainId) {
                        is PersonMemberId -> ChatMemberType.PERSON
                        is PartyMemberId -> ChatMemberType.PARTY
                    }
                )
        }

        private fun ChatMemberInfo.assertMemberIsValid(personService: PersonService, partyService: PartyService) {
            val partyId = (if (type == ChatMemberType.PARTY) partyId else personId)
                    ?: throw MemberWithoutDomainId()
            if (!partyService.notEndedPartyExists(partyId)) {
                throw MembersPartyEnded()
            }
            if (type == ChatMemberType.PERSON && !personService.personWithActivePartyExists(personId!!)) {
                throw InvalidPersonId()
            }
        }

        private fun ChatInfo.assertMemberNotInChat(memberId: MemberId) {
            if (members.map {
                        when(memberId) {
                            is PersonMemberId -> it.personId!!
                            is PartyMemberId -> it.partyId!!
                        }
            }.any { it == memberId.id }) {
                throw MemberAlreadyInChat()
            }
        }

        private fun ChatInfo.assertMemberInChat(uniqueMemberId: String) {
            if (members.any {it.id == uniqueMemberId}) {
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
            if (memberId != null && !personService.personWithActivePartyExists(memberId)) {
                throw InvalidPerson()
            }
            return this
        }

        private infix fun Set<OrderPositionData>.calculatePriceWith(positionService: PositionService): Double {
            return map { it to positionService.getPositionInfo(it.menuPositionId) }
                    .map { it.second.price * it.first.count }.sum()
        }
    }

//    fun Session.saveChanges() {
//        sessionRepository.save(this)
//    }
//
//    fun findSessionByParty(partyId: String): Session {
//        return sessionRepository.findByPartyId(partyId) ?: throw SessionNotFound()
//    }
//
//    fun findSessionByTable(tableId: String): Session {
//        return sessionRepository.findByTableIdAndFinishedIsFalse(tableId) ?: throw SessionNotFound()
//    }
//
//    fun startParty(tableId: String, membersCount: Int?): String {
//        val table = tableService.getTableInfo(tableId)
//        table operate {
//            assertStatusEquals(TableStatus.FREE)
//        }
//        val partyId = UUID.randomUUID().toString()
//        partyService.startParty(tableId)
//    }

    fun registerTable(tableId: String, tableNumber: Int, maxPersons: Int) {
        tableService.registerTable(tableId, tableNumber, maxPersons)
    }

    fun occupyTable(tableId: String) {
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.FREE)
            tableService.occupyTable(tableId)
        }
    }

    fun cleanTable(tableId: String) {
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.AWAITS_CLEANING)
            tableService.cleanTable(tableId)
        }
    }

    fun releaseTable(tableId: String) {
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.OCCUPIED)
            tableService.releaseTable(tableId)
        }
    }

    fun getTableInfo(tableId: String) = tableService.getTableInfo(tableId)

    fun startParty(tableId: String, partyId: String, maxMembersCount: Int, membersCount: Int) {
        tableService.getTableInfo(tableId) operate {
            assertStatusEquals(TableStatus.FREE)
            partyService.startParty(tableId, partyId, maxMembersCount, membersCount)
        }
    }

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
            partyService.removePersonFromParty(partyId, personId)
        }
    }

    fun endParty(partyId: String) {
        partyService.endParty(partyId)
    }

    fun changeMemberBalance(partyId: String, memberId: String, amount: Double) {
        partyService.getParty(partyId) operate {
            assertPersonInParty(memberId)
            partyService.changeMemberBalance(partyId, memberId, amount)
        }
    }

    fun startChat(chatId: String, memberIds: Set<MemberId>): ChatInfo {
        val members = memberIds.map { instantiateMember(UUID.randomUUID().toString(), it) }.toSet()
        members.forEach { it.assertMemberIsValid(personService, partyService) }
        return chatService.startChat(chatId, members)
    }

    fun findChat(chatId: String) = chatService.findChat(chatId)

    fun findChatMember(chatMemberId: String) = chatService.findChatMember(chatMemberId)

    fun addMemberToChat(uniqueMemberId: String, chatId: String, idObject: MemberId) {
        chatService.findChat(chatId) operate {
            assertMemberNotInChat(idObject)
            val member = instantiateMember(UUID.randomUUID().toString(), idObject)
            member.assertMemberIsValid(personService, partyService)
            chatService.addMemberToChat(chatId, member)
        }
    }

    fun removeMemberFromChat(chatId: String, memberId: String) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId)
            chatService.removeMemberFromChat(chatId, memberId)
        }
    }

    fun sendMessage(messageId: String, chatId: String, memberId: String, text: String) {
        chatService.findChat(chatId) operate {
            assertMemberInChat(memberId)
            chatService.sendMessage(messageId, chatId, memberId, text)
        }
    }

    fun createOrder(id: String, orderAuthorData: OrderAuthorData, positions: Set<OrderPositionData>) {
        orderAuthorData checkPartyIn(partyService) checkMemberIn(personService)
        val price = positions calculatePriceWith positionService
        orderService.createOrder(id, orderAuthorData, positions, price)
    }

    fun getBalanceForParty(partyId: String) = orderService.getBalanceForParty(partyId)

    fun getBalanceForPerson(personId: String) = orderService.getBalanceForPerson(personId)

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
}