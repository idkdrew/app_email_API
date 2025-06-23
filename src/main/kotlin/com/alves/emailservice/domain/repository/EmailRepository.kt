package com.alves.emailservice.domain.repository

import com.alves.emailservice.domain.model.Email
import org.springframework.data.jpa.repository.JpaRepository

interface EmailRepository:JpaRepository<Email,Long>{
    fun findAllByEmailRemetente(emailRemetente: String): List<Email>

    fun findAllByEmailDestinatario(emailDestinatario: String): List<Email>
}