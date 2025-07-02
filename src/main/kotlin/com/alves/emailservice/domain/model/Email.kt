package com.alves.emailservice.domain.model

import jakarta.persistence.*

@Entity
data class Email(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val emailId: Long? = null,
    val assunto: String,
    val emailRemetente: String,
    val emailDestinatario: String,

    @Column(columnDefinition = "text")
    val corpo: String,
    var status: String,
    val dataEnvio: String
)
