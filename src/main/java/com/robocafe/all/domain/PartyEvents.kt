package com.robocafe.all.domain

import com.robocafe.all.domain.models.PartyScopedPersonInfo
import com.robocafe.all.domain.models.PartyScopedTableInfo
import com.robocafe.all.events.dispatching.SendToHall
import com.robocafe.all.events.dispatching.SendToParty
import com.robocafe.all.events.dispatching.SendToPerson


@SendToHall("/parties/start")
data class PartyStarted(val partyId: String, val table: PartyScopedTableInfo): DomainEvent {
    override fun convertForHall(): Any {
        return mapOf("tableNum" to table.tableNum)
    }
}
@SendToHall("/parties/member/add")
@SendToParty("/member/add")
data class MemberJoinToParty(
        val table: PartyScopedTableInfo,
        override val partyId: String,
        val member: PartyScopedPersonInfo
): PartyDomainEvent {
    override fun convertForHall(): Any {
        return mapOf(
                "tableNum" to table.tableNum,
                "place" to member.place
        )
    }

    override fun convertForParty(): Any {
        return mapOf(
                "place" to member.place,
                "id" to member.id
        )
    }
}
@SendToHall("/parties/member/remove")
@SendToParty("/member/remove")
data class MemberLeaveParty(
        val table: PartyScopedTableInfo,
        override val partyId: String,
        val member: PartyScopedPersonInfo
): PartyDomainEvent {
    override fun convertForHall(): Any {
        return mapOf(
                "tableNum" to table.tableNum,
                "place" to member.place
        )
    }

    override fun convertForParty(): Any {
        return mapOf(
                "place" to member.place
        )
    }
}
@SendToHall("/parties/end")
@SendToParty("/end")
data class PartyEnded(val table: PartyScopedTableInfo,
                      override val partyId: String): PartyDomainEvent {
    override fun convertForHall(): Any {
        return mapOf(
                "tableNum" to table.tableNum
        )
    }

    override fun convertForParty(): Any {
        return mapOf<String, Any>()
    }
}
@SendToParty("/member/balance/change")
@SendToPerson("/balance/change")
data class MemberBalanceChanged(
        override val partyId: String,
        override val personId: String,
        val amount: Double // Balance increment value
): PartyDomainEvent, PersonDomainEvent {
    override fun convertForParty(): Any {
        return mapOf(
                "memberId" to personId,
                "amount" to amount
        )
    }

    override fun convertForPerson(): Any {
        return mapOf(
                "increment" to amount
        )
    }
}