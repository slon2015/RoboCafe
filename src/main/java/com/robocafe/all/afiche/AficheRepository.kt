package com.robocafe.all.afiche

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AficheRepository: JpaRepository<Afiche, String> {
}