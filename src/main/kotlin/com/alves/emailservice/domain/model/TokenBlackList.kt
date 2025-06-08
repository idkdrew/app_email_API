package com.alves.emailservice.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "token_blacklist")
data class TokenBlacklist(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val token: String,

    @Column(nullable = false)
    val expiration: LocalDateTime
)
