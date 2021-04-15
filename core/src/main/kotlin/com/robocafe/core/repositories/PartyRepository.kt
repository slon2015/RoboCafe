package com.robocafe.core.repositories

import com.robocafe.core.domain.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository : JpaRepository<Party, String> {
    fun existsByIdAndEndTimeIsNull(id: String): Boolean
    fun findByTableIdAndEndTimeIsNull(tableId: String): Party?
    fun findByEndTimeIsNullAndMembersIdEquals(personId: String): Party?
}