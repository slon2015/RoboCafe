package com.robocafe.all.domain

data class PartyStarted(val partyId: String, val tableId: String)
data class MemberJoinToParty(val partyId: String, val memberId: String)
data class MemberLeaveParty(val partyId: String, val memberId: String)
data class PartyEnded(val partyId: String)