package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Chat
import com.robocafe.all.domain.ChatMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<Chat, String> {
    fun findByMembersContains(chatMember: ChatMember): Set<Chat>
}