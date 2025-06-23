package com.alves.emailservice.domain.repository

import com.alves.emailservice.domain.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmail(email: String): Usuario?
    fun findByEmailAndAtivoTrue(email: String): Usuario?
    fun findAllByAtivoTrue(): List<Usuario>
}