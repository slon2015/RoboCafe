package com.robocafe.core.repositories

import com.robocafe.core.domain.Chat
import com.robocafe.core.domain.ChatMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<Chat, String> {
    fun findByMembersContains(chatMember: ChatMember): Set<Chat>
}