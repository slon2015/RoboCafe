package com.robocafe.all.application.repositories

import com.robocafe.all.domain.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, String>