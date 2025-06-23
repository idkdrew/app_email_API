package com.alves.emailservice.domain.model

import jakarta.persistence.*

@Entity
data class Rascunho(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val rascunhoId: Long? = null,

    @Column(nullable = false)
    val usuarioId: Long,

    val assunto: String?,
    val emailDestinatario: String?,
    val corpo: String?
)
