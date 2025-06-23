package com.alves.emailservice.domain.repository

import com.alves.emailservice.domain.model.Rascunho
import org.springframework.data.jpa.repository.JpaRepository

interface RascunhoRepository : JpaRepository<Rascunho, Long>{
    fun findAllByUsuarioId(usuarioId: Long): List<Rascunho>
}
