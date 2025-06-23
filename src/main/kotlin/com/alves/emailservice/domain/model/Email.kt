package com.alves.emailservice.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Email(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val emailId: Long? = null,
    val assunto: String,
    val emailRemetente: String,
    val emailDestinatario: String,
    val corpo: String,
    var status: String,
    val dataEnvio: String
)
