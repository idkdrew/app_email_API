package com.alves.emailservice.service

import com.alves.emailservice.domain.repository.UsuarioRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UsuarioDetailsService(private val usuarioRepository: UsuarioRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val usuario = usuarioRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("Usuário não encontrado")
        return User(usuario.email, usuario.senha, emptyList())
    }
}
