package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository : JpaRepository<Party, String> {
    fun existsByIdAndEndTimeIsNull(id: String): Boolean
    fun findByTableIdAndEndTimeIsNull(tableId: String): Party?
    fun findByEndTimeIsNullAndMembersIdEquals(personId: String): Party?
}