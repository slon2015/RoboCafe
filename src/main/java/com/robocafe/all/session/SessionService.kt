package com.robocafe.all.session

import com.robocafe.all.application.services.PartyService
import com.robocafe.all.application.services.PersonService
import com.robocafe.all.application.services.TableInfo
import com.robocafe.all.application.services.TableService
import com.robocafe.all.domain.MemberJoinToParty
import com.robocafe.all.domain.PartyStarted
import com.robocafe.all.domain.TableStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class SessionService @Autowired constructor(
        private val tableService: TableService,
        private val partyService: PartyService,
        private val personService: PersonService,
        private val sessionRepository: SessionRepository
) {

    private companion object Dsl {
        infix fun <T> T.operate(operation: T.() -> Unit) {
            operation(this)
        }

        fun TableInfo.assertStatusEquals(expected: TableStatus) {
            if (tableStatus != expected) {
                throw IncorrectTableStatus()
            }
        }
    }

    fun Session.saveChanges() {
        sessionRepository.save(this)
    }

    fun findSessionByParty(partyId: String): Session {
        return sessionRepository.findByPartyId(partyId) ?: throw SessionNotFound()
    }

    fun findSessionByTable(tableId: String): Session {
        return sessionRepository.findByTableIdAndFinishedIsFalse(tableId) ?: throw SessionNotFound()
    }

    fun startParty(tableId: String, membersCount: Int?): String {
        val table = tableService.getTableInfo(tableId)
        table operate {
            assertStatusEquals(TableStatus.FREE)
        }
        val partyId = UUID.randomUUID().toString()
        partyService.startParty(tableId)
    }

    @EventListener(PartyStarted::class)
    fun partyStarted(event: PartyStarted) {
        val newSession = Session(
                UUID.randomUUID().toString(),
                event.tableId,
                event.partyId
        ).saveChanges()
    }

    @EventListener(MemberJoinToParty::class)
    fun memberJoinedToParty(event: MemberJoinToParty) {
        findSessionByParty(event.partyId) operate {
            personIds.add(event.memberId)
            saveChanges()
        }
    }
}