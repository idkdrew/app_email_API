package com.alves.emailservice.domain.repository

import com.alves.emailservice.domain.model.TokenAtivo
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TokenAtivoRepository : JpaRepository<TokenAtivo, String> {
    fun findAllByExpirationAfter(now: LocalDateTime = LocalDateTime.now()): List<TokenAtivo>
}
