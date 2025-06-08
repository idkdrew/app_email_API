package com.alves.emailservice.domain.repository

import com.alves.emailservice.domain.model.TokenBlacklist
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenBlacklistRepository : JpaRepository<TokenBlacklist, Long> {
    fun findByToken(token: String): Optional<TokenBlacklist>
}