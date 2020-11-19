package com.robocafe.all.application.repositories

import com.robocafe.all.domain.ChatMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMembersRepository : JpaRepository<ChatMember, String>