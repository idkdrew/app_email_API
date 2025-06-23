package com.alves.emailservice.domain.model

import jakarta.persistence.*

@Entity
data class Usuario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false)
    var senha: String,

    @Column(nullable = false, columnDefinition = "boolean default true")
    var ativo: Boolean = true
)