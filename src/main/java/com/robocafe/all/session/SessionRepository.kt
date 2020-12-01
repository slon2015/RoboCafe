package com.robocafe.all.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository: JpaRepository<Session, String> {
    fun findByPartyId(partyId: String): Session?
    fun findByTableIdAndFinishedIsFalse(tableId: String): Session?
}